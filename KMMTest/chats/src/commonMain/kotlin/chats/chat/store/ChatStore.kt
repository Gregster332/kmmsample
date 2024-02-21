package chats.chat.store

import com.arkivanov.mvikotlin.core.store.Store

interface ChatStore : Store<ChatStore.Intent, ChatStore.State, Nothing> {
    sealed interface Intent {
        data class SendMessage(val message: String) : Intent
        data object Close: Intent
    }

    data class State(
        val messages: List<String> = emptyList(),
        val isLoading: Boolean = false,
    )

    sealed interface Action {
        data object ConnectChat: Action
        data class LoadMessages(val chatId: String): Action
    }
}
