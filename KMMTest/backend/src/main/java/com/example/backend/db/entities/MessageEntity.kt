package com.example.backend.db.entities

import com.example.backend.db.tables.Messages
import com.example.backend.utils.serializers.UUIDSerializer
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

@Serializable
data class MessageMap(
    val messageText: String,
    @Serializable(UUIDSerializer::class)
    val senderId: UUID
)

class MessageEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MessageEntity>(Messages)
    var message_text by Messages.message_text
    var senderId by Messages.senderId

    fun toMessage() = MessageMap(
        messageText = message_text,
        senderId
    )
}