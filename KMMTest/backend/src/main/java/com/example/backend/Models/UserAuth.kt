package com.example.backend.Models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserAUTH(
    val nickname: String,
    @SerialName("phone_number") val phoneNumber: String,
    val password: String
)