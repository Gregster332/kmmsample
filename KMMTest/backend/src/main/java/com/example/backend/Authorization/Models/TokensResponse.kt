package com.example.backend.Authorization.Models

import kotlinx.serialization.Serializable

@Serializable
data class TokensResponse (
    val accessToken: String,
    val refreshToken: String
)