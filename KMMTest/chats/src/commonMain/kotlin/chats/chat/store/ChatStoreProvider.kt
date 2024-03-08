package chats.chat.store

import chats.chat.store.ChatStore.Intent
import chats.chat.store.ChatStore.State
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.example.core.MVI.BaseExecutor
import com.example.core.Services.DateConverter
import com.example.core.Services.DateFormats
import com.example.corenetwork.api.auth.LocalCache
import com.example.corenetwork.api.chats.ChatsApi
import com.example.corenetwork.api.chats.MessageUnit
import com.example.corenetwork.api.websocket.WebSocketNew
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal object ChatStoreProvider {
    fun create(
        storeFactory: StoreFactory,
        webSocketNew: WebSocketNew,
        chatsApi: ChatsApi,
        localCache: LocalCache,
        chatId: String
    ): ChatStore = object :
        ChatStore,
        Store<Intent, State, Nothing> by storeFactory.create(
            name = ChatStore::class.simpleName,
            initialState = State(),
            bootstrapper = SimpleBootstrapper(
                ChatStore.Action.ConnectChat,
                ChatStore.Action.LoadMessages(chatId)
            ),
            executorFactory = {
                ExecutorImpl(webSocketNew, chatsApi = chatsApi, localCache, chatId)
            },
            reducer = ReducerImpl,
        ) {}

    private sealed interface Msg {
        data class GetNewMessage(
            val message: ChatStore.ChatMessage,
        ) : Msg
        data object GetConnectedSuccessful : Msg
        data class GetErrorWhileConnect(val error: Throwable) : Msg
    }

    private class ExecutorImpl(
        private val webSocketNew: WebSocketNew,
        private val chatsApi: ChatsApi,
        private val localCache: LocalCache,
        private val chatId: String
    ) : BaseExecutor<Intent, ChatStore.Action, State, Msg, Nothing>() {
        private val currentUser = localCache.getCurrentUser()

        override suspend fun suspendExecuteAction(action: ChatStore.Action) = when(action) {
            is ChatStore.Action.ConnectChat -> connect()
            is ChatStore.Action.LoadMessages -> loadMessages(action.chatId)
        }

        override suspend fun suspendExecuteIntent(intent: Intent) = when (intent) {
            is Intent.SendMessage -> send(intent.message)
            is Intent.Close -> closeConnection()
        }

        private suspend fun connect() {
            scope.launch {
                webSocketNew.connect { message, error ->
                    val newMessage = ChatStore.ChatMessage(
                        isOutgoing = currentUser?.id == message?.senderId ?: "",
                        chatId = message?.chatId ?: "",
                        senderId = message?.senderId ?: "",
                        messageText = message?.message ?: "",
                        nickname = message?.nickname ?: "",
                        date = DateConverter.convert(message?.timestamp ?: "", DateFormats.HH_MM),
                        state = if (error != null) ChatStore.MessageState.Sent else ChatStore.MessageState.NotSent(error)
                    )

                    dispatch(Msg.GetNewMessage(newMessage))
                }
            }
        }

        private suspend fun closeConnection() {
            webSocketNew.close()
        }

        private suspend fun send(message: String) {
            val formatted = formattedString(chatId, message)
            webSocketNew.sendMessage(formatted)
        }

        private suspend fun loadMessages(chatId: String) {
            try {
                val result = scope.async {
                    chatsApi.getAllMessages(chatId)
                }

                result.await().map {
                    ChatStore.ChatMessage(
                        nickname = it.nickname,
                        isOutgoing = currentUser?.id == it.senderId,
                        chatId = it.chatId,
                        senderId = it.senderId,
                        messageText = it.message,
                        date = DateConverter.convert(it.timestamp, DateFormats.HH_MM),
                        state = ChatStore.MessageState.Sent
                    )
                }.forEach {
                    dispatch(Msg.GetNewMessage(it))
                }
            } catch(e: Exception) {
                println(e)
            }
        }

        private fun formattedString(chatId: String, message: String): String {
            val sender = localCache.getCurrentUser()
            val sendDate = DateConverter.current
            val message = Json.encodeToString(
                MessageUnit(chatId, message, sender?.id ?: "", sender?.nickname ?: "", sendDate)
            )
            return message
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(message: Msg): State = when (message) {
            is Msg.GetNewMessage -> putNewMessage(message.message)
            is Msg.GetConnectedSuccessful -> copy(isLoading = true)
            is Msg.GetErrorWhileConnect -> copy(isLoading = true, error = message.error)
        }

        private fun State.putNewMessage(m: ChatStore.ChatMessage): State {
            val oldMessages = messages.toMutableList()
            oldMessages.add(m)
            return copy(messages = oldMessages, isLoading = false)
        }
    }
}
