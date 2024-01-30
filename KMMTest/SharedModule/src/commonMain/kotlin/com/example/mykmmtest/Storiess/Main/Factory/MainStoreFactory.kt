package com.example.mykmmtest.Storiess.Main.Factory

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.example.corenetwork.api.Chats.WebSocketService
import com.example.corenetwork.model.Chats.ChatUnit
import com.example.mykmmtest.Services.WsMessage
import com.example.mykmmtest.Storiess.Main.MainExecutor
import com.example.mykmmtest.Storiess.Main.MainReducer
import com.example.mykmmtest.Storiess.Main.MainStore

internal class MainStoreFactory(
    private val storeFactory: StoreFactory,
    //private val repo: PostLoader,
    private val webSocketService: WebSocketService
) {
    fun create(): MainStore = object :
        MainStore,
        Store<MainStore.Intent, MainStore.State, Nothing> by storeFactory.create(
            name = MainStore::class.simpleName,
            initialState = MainStore.State(),
            bootstrapper = null,
            executorFactory = {
                MainExecutor()
            },
            reducer = MainReducer()
        ) {}

    sealed interface Message {
        object SetLoading : Message
        object SetError : Message
        data class OnChatsLoaded(val chats: List<ChatUnit>): Message
        data class DidReceiveData(val message: WsMessage): Message
    }
}
