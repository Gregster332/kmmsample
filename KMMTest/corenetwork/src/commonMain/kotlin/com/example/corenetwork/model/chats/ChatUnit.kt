package com.example.corenetwork.model.chats

import kotlinx.serialization.Serializable

@Serializable
data class ChatUnit(
    val id: String,
    val name: String,
    val ownerId: String,
)


