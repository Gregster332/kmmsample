package com.example.corenetwork.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequestEntity(
    val nickname: String = "",
    val email: String = "",
    val password: String = "",
)
