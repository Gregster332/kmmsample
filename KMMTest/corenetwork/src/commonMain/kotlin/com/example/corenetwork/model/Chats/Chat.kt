package com.example.corenetwork.model.Chats

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Chat(
    @SerialName("chat_id") val chatId: Int,
    val name: String,
    @SerialName("creator_id") val creatorId: String
)
