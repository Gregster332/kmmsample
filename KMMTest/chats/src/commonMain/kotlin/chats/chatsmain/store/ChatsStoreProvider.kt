package chats.chatsmain.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.example.core.MVI.BaseExecutor
import com.example.corenetwork.api.chats.ChatsApi
import com.example.corenetwork.model.chats.ChatUnit

internal class ChatsStoreProvider(
    private val storeFactory: StoreFactory,
    private val chatsApi: ChatsApi,
) {
    fun provide(): ChatsStore = object :
            ChatsStore,
            Store<ChatsStore.ChatsIntent, ChatsStore.ChatsUiState, Nothing> by storeFactory.create(
                name = ChatsStore::class.simpleName,
                autoInit = true,
                initialState = ChatsStore.ChatsUiState(),
                bootstrapper = SimpleBootstrapper(ChatsStore.Action.LoadChats),
                executorFactory = {
                    ExecutorImpl(chatsApi)
                },
                reducer = ReducerImpl,
            ) { }

    private sealed class Message {
        data class Chats(val list: List<ChatUnit>) : Message()
        data class Error(val error: String) : Message()
    }

    private inner class ExecutorImpl(
        private val chatsApi: ChatsApi,
    ) : BaseExecutor<ChatsStore.ChatsIntent, ChatsStore.Action, ChatsStore.ChatsUiState, Message, Nothing>() {
        override suspend fun suspendExecuteIntent(
            intent: ChatsStore.ChatsIntent
        ) = when (intent) {
            is ChatsStore.ChatsIntent.TryLoadChats -> getAllChats()
        }

        override suspend fun suspendExecuteAction(action: ChatsStore.Action) =
            when (action) {
                is ChatsStore.Action.LoadChats -> getAllChats()
            }

        private suspend fun getAllChats() {
            try {
                val chats = chatsApi.getAllChats()
                dispatch(Message.Chats(chats))
            } catch (e: Exception) {
                println(e)
                dispatch(Message.Error(e.message ?: ""))
            }
        }
    }

    private object ReducerImpl : Reducer<ChatsStore.ChatsUiState, Message> {
        override fun ChatsStore.ChatsUiState.reduce(msg: Message) = when (msg) {
                is Message.Chats -> copy(isLoading = false, chats = msg.list, errorMessage = null)
                is Message.Error -> copy(isLoading = false, errorMessage = msg.error, chats = emptyList())
            }
    }
}
