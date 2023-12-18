package com.example.mykmmtest.Stories.Main.Model.Presentation

import com.example.mykmmtest.Models.Post
import com.example.mykmmtest.Stories.Main.Model.MainStore
import com.example.mykmmtest.Utils.Mapper

internal class MainUIStateMapper: Mapper<MainStore.State, UIMainState> {
    override fun map(item: MainStore.State) = UIMainState(
        posts = item.posts,
        isLoading = item.isLoading
    )
}

data class UIMainState internal constructor(
    val posts: List<Post>? = null,
    val isLoading: Boolean = false
)