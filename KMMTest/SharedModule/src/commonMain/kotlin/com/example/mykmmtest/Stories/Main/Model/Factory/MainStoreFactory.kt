package com.example.mykmmtest.Stories.Main.Model.Factory

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.example.mykmmtest.Models.Post
import com.example.mykmmtest.Services.PostLoader
import com.example.mykmmtest.Stories.Main.Model.MainExecutor
import com.example.mykmmtest.Stories.Main.Model.MainReducer
import com.example.mykmmtest.Stories.Main.Model.MainStore

internal class MainStoreFactory(
    private val storeFactory: StoreFactory,
    private val repo: PostLoader
) {
    fun create() : MainStore = object :
        MainStore,
        Store<MainStore.Intent, MainStore.State, Nothing> by storeFactory.create(
            name = MainStore::class.simpleName,
            initialState = MainStore.State(posts = null),
            bootstrapper = null,
            executorFactory = {
                MainExecutor(repo)
            },
            reducer = MainReducer()
        ) {}

    sealed interface Message {
        object SetLoading : Message
        data class SetUserInfo(val posts: List<Post>) : Message
        object SetError : Message
    }
}
