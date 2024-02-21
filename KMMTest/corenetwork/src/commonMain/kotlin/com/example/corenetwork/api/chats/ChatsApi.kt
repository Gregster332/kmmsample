package com.example.corenetwork.api.chats

import com.example.corenetwork.model.chats.ChatUnit

interface ChatsApi {
    suspend fun createNewChat(entity: CreateFaceToFaceChatRequest): ChatUnit?
    suspend fun getAllChats(): List<ChatUnit>
    suspend fun getAllMessages(chatId: String): List<MessageUnit>
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
    data class Success<out R>(val data: R) : Result<R>
    data class Failure(val throwable: Throwable) : Result<Nothing>
}
