package com.example.mykmmtest.Storiess.Main.Presentation

import com.example.mykmmtest.Models.Post
import com.example.mykmmtest.Services.WsMessage
import com.example.mykmmtest.Storiess.Main.MainStore
import com.example.mykmmtest.Utils.Mapper

internal class MainUIStateMapper: Mapper<MainStore.State, UIMainState> {
    override fun map(item: MainStore.State) = UIMainState(
        posts = item.posts,
        isLoading = item.isLoading,
        messages = item.messages
    )
}

data class UIMainState(
    val posts: List<Post>? = null,
    val isLoading: Boolean = false,
    val messages: List<WsMessage> = emptyList()
)