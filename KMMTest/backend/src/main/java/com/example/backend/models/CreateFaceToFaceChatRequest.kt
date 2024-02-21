package com.example.backend.models

import com.example.backend.NewChatRequestModel
import com.example.backend.utils.serializers.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class CreateFaceToFaceChatRequest(
    val name: String,
    @Serializable(with = UUIDSerializer::class)
    val ownerId: UUID,
    @Serializable(with = UUIDSerializer::class)
    val opponent: UUID,
)

fun CreateFaceToFaceChatRequest.mapTo() = NewChatRequestModel(
    name = name,
    ownerId = ownerId,
)