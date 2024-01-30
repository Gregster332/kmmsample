package com.example.backend.Models

import kotlinx.serialization.Serializable

@Serializable
data class LoginState(
    val isAuthorized: Boolean = false,
    val isAccessTokenExpired: Boolean = false
)