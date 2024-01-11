package com.example.mykmmtest.Storiess.Main

import com.arkivanov.mvikotlin.core.store.Store
import com.example.mykmmtest.Models.Post
import com.example.mykmmtest.Services.ChatUnit
import com.example.mykmmtest.Services.WsMessage

interface MainStore: Store<MainStore.Intent, MainStore.State, Nothing>{
    data class State internal constructor(
        val posts: List<Post>? = null,
        val isLoading: Boolean = false,
        val messages: List<WsMessage> = emptyList(),
        val chats: List<ChatUnit> = emptyList()
    )

    sealed interface Intent {
        data object LoadChats: Intent
        data class OnCreateNewChat(val name: String): Intent
        data class OnTapSendMessage(val string: String): Intent
    }
}