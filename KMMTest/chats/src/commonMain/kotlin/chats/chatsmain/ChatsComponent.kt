package chats.chatsmain

import chats.chatsmain.store.ChatsStore
import chats.chatsmain.store.ChatsStoreProvider
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.subscribe
import com.arkivanov.mvikotlin.core.binder.Binder
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.example.corenetwork.api.chats.ChatsApi
import com.example.corenetwork.api.chats.chatsUnitMock
import com.example.corenetwork.model.chats.ChatUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map

class PreviewChatsComponent : Chats {
    override val chats: Value<ChatsStore.ChatsUiState> = MutableValue(
        ChatsStore.ChatsUiState(
            isLoading = false,
            null,
            chats = ChatUnit.chatsUnitMock()
        )
    )

    override fun tryLoadChats() {
    }

    override fun openChat(chatUnit: ChatUnit) {
    }
}

class ChatsComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    chatsApi: ChatsApi,
    private val onChatClicked: (ChatUnit) -> Unit,
) : Chats, ComponentContext by componentContext {
    private val store =
        instanceKeeper.getStore {
            ChatsStoreProvider(
                storeFactory = storeFactory,
                chatsApi = chatsApi,
            ).provide()
        }

    private lateinit var binder: Binder

    private val _mutableState = MutableValue(ChatsStore.ChatsUiState())
    override val chats: Value<ChatsStore.ChatsUiState> = _mutableState

    init {
        lifecycle.subscribe(
            onCreate = {
                binder =
                    bind(Dispatchers.Main.immediate) {
                        store.states.map { it } bindTo (::acceptState)
                    }
                // store.init()
                binder.start()
            },
            onDestroy = {
                binder.stop()
                store.dispose()
            },
        )
    }

    override fun tryLoadChats() {
        store.accept(ChatsStore.ChatsIntent.TryLoadChats)
    }

    override fun openChat(chatUnit: ChatUnit) {
        onChatClicked(chatUnit)
    }

    private fun acceptState(state: ChatsStore.ChatsUiState) {
        _mutableState.value = state
    }
}
