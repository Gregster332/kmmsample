package chats.chatsmain

import chats.chatsmain.store.ChatsStore
import com.arkivanov.decompose.value.Value
import com.example.corenetwork.model.chats.ChatUnit

interface Chats {
    val chats: Value<ChatsStore.ChatsUiState>
    // val childStack: Value<ChildStack<*, Child>>

    fun tryLoadChats()
    fun openChat(chatUnit: ChatUnit)
}
