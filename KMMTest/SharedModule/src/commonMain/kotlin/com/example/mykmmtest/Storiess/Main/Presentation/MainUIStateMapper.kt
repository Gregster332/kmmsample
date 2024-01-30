package com.example.mykmmtest.Storiess.Main.Presentation

import com.example.corenetwork.model.Chats.ChatUnit
import com.example.mykmmtest.Services.WsMessage
import com.example.mykmmtest.Storiess.Main.MainStore
import com.example.mykmmtest.Utils.Mapper

internal class MainUIStateMapper: Mapper<MainStore.State, UIMainState> {
    override fun map(item: MainStore.State) = UIMainState(
        isLoading = item.isLoading,
        messages = item.messages,
        chats = item.chats
    )
}

data class UIMainState(
    val isLoading: Boolean = false,
    val messages: List<WsMessage> = emptyList(),
    val chats: List<ChatUnit> = emptyList()
)