package chats.chat.store

import com.arkivanov.mvikotlin.core.store.Store

interface ChatStore : Store<ChatStore.Intent, ChatStore.State, Nothing> {
    sealed interface Intent {
        data class SendMessage(val message: String) : Intent
        data object Close: Intent
    }

    data class State(
        val messages: List<ChatMessage> = emptyList(),
        val isLoading: Boolean = false,
        val error: Throwable? = null
    )

    data class ChatMessage(
        val nickname: String = "",
        val isOutgoing: Boolean = false,
        val chatId: String,
        val senderId: String,
        val messageText: String,
        val date: String,
        val state: MessageState
    )

    sealed interface MessageState {
        data object Sent: MessageState
        data class NotSent(val error: Throwable?): MessageState
    }

    sealed interface Action {
        data object ConnectChat: Action
        data class LoadMessages(val chatId: String): Action
    }
}
