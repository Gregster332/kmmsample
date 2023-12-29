package com.example.mykmmtest.Services.AuthService.Models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SendAuthRequestEntity(
    val nickname: String,
    @SerialName("phone_number") val phoneNumber: String,
    val password: String
    )