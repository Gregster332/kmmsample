package com.example.mykmmtest.Storiess.Main.Factory

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.example.mykmmtest.Models.Post
import com.example.mykmmtest.Services.ChatUnit
import com.example.mykmmtest.Services.PostLoader
import com.example.mykmmtest.Services.WebSocketService
import com.example.mykmmtest.Services.WsMessage
import com.example.mykmmtest.Storiess.Main.MainExecutor
import com.example.mykmmtest.Storiess.Main.MainReducer
import com.example.mykmmtest.Storiess.Main.MainStore

internal class MainStoreFactory(
    private val storeFactory: StoreFactory,
    private val repo: PostLoader,
    private val webSocketService: WebSocketService
) {
    fun create(): MainStore = object :
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
        data class OnChatsLoaded(val chats: List<ChatUnit>): Message
        data class DidReceiveData(val message: WsMessage): Message
    }
}
