package com.example.corenetwork.api.Chats

import com.example.corenetwork.model.Chats.Chat
import com.example.corenetwork.model.Chats.ChatUnit

interface ChatsApi {
    //suspend fun getAllUserChats(): List<ChatUnit>
}

interface WebSocketService {
    var onOpenBlock: (() -> Unit)?
    var onFailureBlock: ((Throwable) -> Unit)?
    var onCloseBlock: (() -> Unit)?
    var messageListenerBlock: ((msg: String) -> Unit)?

    fun connect(url: String)
    fun disconnect()
    fun send(msg: String)
}

sealed interface Result<out T> {
    data class Success<out R>(val data: R): Result<R>
    data class Failure(val throwable: Throwable): Result<Nothing>
}
