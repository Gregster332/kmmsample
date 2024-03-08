package com.example.corenetwork.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthState(
    val isAuthorized: Boolean,
    val isAccessTokenExpired: Boolean
)
