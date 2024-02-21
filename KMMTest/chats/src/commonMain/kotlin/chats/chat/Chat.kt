package chats.chat

import chats.chat.store.ChatStore
import com.arkivanov.decompose.value.Value

interface Chat {
    val currentMessages: Value<ChatStore.State>

    fun send(message: String)
    fun close()
}
