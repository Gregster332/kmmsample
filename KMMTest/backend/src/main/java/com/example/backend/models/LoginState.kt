package com.example.backend.models

import kotlinx.serialization.Serializable

@Serializable
data class LoginState(
    val isAuthorized: Boolean = false,
    val isAccessTokenExpired: Boolean = false,
)
