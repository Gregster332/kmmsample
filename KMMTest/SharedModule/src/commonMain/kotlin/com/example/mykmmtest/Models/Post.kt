package com.example.mykmmtest.Models

import com.example.mykmmtest.Expectations.randomUUID
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Post (
    val internalID: String = randomUUID(),
    @SerialName("userId") val userID: Long,
    @SerialName("id") val id: Long,
    @SerialName("title") val title: String,
    @SerialName("body") val body: String
)