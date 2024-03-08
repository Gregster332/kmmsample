package com.example.backend.models

import com.example.backend.utils.serializers.DateSerializer
import com.example.backend.utils.serializers.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.Date
import java.util.UUID

@Serializable
data class MessageUnit(
    @Serializable(UUIDSerializer::class)
    val chatId: UUID,
    val message: String,
    @Serializable(UUIDSerializer::class)
    val senderId: UUID,
    val nickname: String,
    @Serializable(DateSerializer::class)
    val timestamp: LocalDateTime = LocalDateTime.now()
)