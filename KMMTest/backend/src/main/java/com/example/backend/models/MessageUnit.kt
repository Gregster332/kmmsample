package com.example.backend.models

import com.example.backend.utils.serializers.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class MessageUnit(
    @Serializable(UUIDSerializer::class)
    val chatId: UUID,
    val message: String,
    @Serializable(UUIDSerializer::class)
    val senderId: UUID
)