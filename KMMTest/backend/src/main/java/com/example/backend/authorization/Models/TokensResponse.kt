package com.example.backend.authorization.Models

import com.example.backend.utils.serializers.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class TokensResponse(
    val accessToken: String,
    val refreshToken: String,
    @Serializable(UUIDSerializer::class)
    val userId: UUID
)
