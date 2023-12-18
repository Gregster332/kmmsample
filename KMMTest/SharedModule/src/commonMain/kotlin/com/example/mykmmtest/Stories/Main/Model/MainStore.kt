package com.example.mykmmtest.Stories.Main.Model

import com.arkivanov.mvikotlin.core.store.Store
import com.example.mykmmtest.Models.Post

interface MainStore: Store<MainStore.Intent, MainStore.State, Nothing>{
    data class State internal constructor(
        val posts: List<Post>?,
        val isLoading: Boolean = false
    )

    sealed interface Intent {
        object Load: Intent
    }
}