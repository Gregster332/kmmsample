package com.example.mykmmtest.Models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Post (
    @SerialName("userId") val userID: Long,
    @SerialName("id") val id: Long,
    @SerialName("title") val title: String,
    @SerialName("body") val body: String
)