package com.example.chats.api

import com.arkivanov.decompose.value.Value
import com.example.chats.api.store.ChatsStore
import kotlinx.coroutines.flow.StateFlow

interface Chats {
    val chats: Value<ChatsStore.ChatsUiState>

    fun tryLoadChats()
}