package com.example.corenetwork.model.Auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthState(
    val isAuthorized: Boolean,
    val isAccessTokenExpired: Boolean
)