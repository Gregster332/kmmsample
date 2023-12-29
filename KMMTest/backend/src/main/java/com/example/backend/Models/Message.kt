package com.example.backend.Models

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val userID: String,
    val userName: String,
    val messageText: String
)
