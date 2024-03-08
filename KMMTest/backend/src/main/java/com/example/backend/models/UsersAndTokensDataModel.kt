package com.example.backend.models

import com.example.backend.utils.serializers.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class UsersAndTokensDataModel(
    @Serializable(UUIDSerializer::class)
    val userId: UUID,
    val accessToken: String?,
    val refreshToken: String?
)