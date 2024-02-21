package chats.chatsmain.store

import com.arkivanov.mvikotlin.core.store.Store
import com.example.corenetwork.model.chats.ChatUnit

interface ChatsStore : Store<ChatsStore.ChatsIntent, ChatsStore.ChatsUiState, Nothing> {
    sealed interface ChatsIntent {
        data object TryLoadChats : ChatsIntent
    }

    sealed interface Action {
        data object LoadChats : Action
    }

    data class ChatsUiState(
        val isLoading: Boolean = true,
        val errorMessage: String? = null,
        val chats: List<ChatUnit> = emptyList(),
    )
}
