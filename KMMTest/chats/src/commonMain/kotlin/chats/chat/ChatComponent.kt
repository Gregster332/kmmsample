package chats.chat

import chats.chat.store.ChatStore
import chats.chat.store.ChatStoreProvider
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.doOnCreate
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.essenty.lifecycle.doOnStart
import com.arkivanov.essenty.lifecycle.doOnStop
import com.arkivanov.mvikotlin.core.binder.Binder
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.example.corenetwork.api.auth.LocalCache
import com.example.corenetwork.api.chats.ChatsApi
import com.example.corenetwork.api.websocket.WebSocketNew
import com.example.corenetwork.model.chats.ChatUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map

class ChatComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    webSocketNew: WebSocketNew,
    chatsApi: ChatsApi,
    localCache: LocalCache,
    private val chat: ChatUnit
) : Chat, ComponentContext by componentContext {
    private val store =
        instanceKeeper.getStore {
            ChatStoreProvider.create(
                storeFactory,
                webSocketNew,
                chatsApi,
                localCache,
                chat.id
            )
        }

    override val currentMessages: Value<ChatStore.State>
        get() = _currentMessages

    private val _currentMessages = MutableValue(ChatStore.State())
    private lateinit var binder: Binder

    init {
        lifecycle.doOnCreate {
            println("CHAT STORE INIT")
            binder =
                bind(Dispatchers.Main.immediate) {
                    store.states.map { it } bindTo (::acceptState)
                }
            binder.start()
        }
        lifecycle.doOnDestroy {
            println("ONSTOP CHAT SCREEN")
            binder.stop()
            store.dispose()
        }
    }

    override fun send(message: String) {
        store.accept(ChatStore.Intent.SendMessage(message))
    }

    override fun close() {
        store.accept(ChatStore.Intent.Close)
    }

    private fun acceptState(state: ChatStore.State) {
        _currentMessages.value = state
    }
}
