package com.example.mykmmtest.Storiess.Main

import com.arkivanov.mvikotlin.core.store.Reducer
import com.example.mykmmtest.Services.WsMessage
import com.example.mykmmtest.Storiess.Main.Factory.MainStoreFactory

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
        is MainStoreFactory.Message.DidReceiveData -> copy(
            messages = reduceMessages(copy().messages, msg.message)
        )
        is MainStoreFactory.Message.OnChatsLoaded -> copy(
            chats = msg.chats
        )
    }

    private fun reduceMessages(hist: List<WsMessage>, new: WsMessage): List<WsMessage> {
        var newHist = hist.toMutableList()
        newHist.add(0, new)
        return newHist
    }
}