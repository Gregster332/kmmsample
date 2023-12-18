package com.example.mykmmtest.Stories.Main.Model

import com.arkivanov.mvikotlin.core.store.Reducer
import com.example.mykmmtest.Stories.Main.Model.Factory.MainStoreFactory

internal class MainReducer : Reducer<MainStore.State, MainStoreFactory.Message> {
    override fun MainStore.State.reduce(
        msg: MainStoreFactory.Message
    ) = when(msg) {
        is MainStoreFactory.Message.SetUserInfo -> copy(
            posts = msg.posts,
            isLoading = false
        )
        is MainStoreFactory.Message.SetError -> copy(
            posts = null,
            isLoading = false
        )
        is MainStoreFactory.Message.SetLoading -> copy(
            posts = null,
            isLoading = true
        )
    }
}