package com.example.corenetwork.model.Auth

import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequestEntity(
    val nickname: String = "",
    val email: String = "",
    val password: String = ""
)