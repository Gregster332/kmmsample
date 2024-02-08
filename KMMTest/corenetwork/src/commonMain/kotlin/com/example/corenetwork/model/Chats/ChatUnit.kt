package com.example.corenetwork.model.Chats

import kotlinx.serialization.Serializable

@Serializable
data class ChatUnit(
    val name: String,
    val ownerId: String,
   // val participants: List<User>
)