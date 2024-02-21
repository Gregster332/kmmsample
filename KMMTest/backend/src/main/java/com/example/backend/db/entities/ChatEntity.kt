package com.example.backend.db.entities

import com.example.backend.NewChatRespondModel
import com.example.backend.db.tables.Chats
import com.example.backend.db.tables.ChatsAndMessages
import com.example.backend.db.tables.ChatsAndParticipants
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

class ChatEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ChatEntity>(Chats)
    var name by Chats.name
    var ownerId by Chats.ownerId
    var messages by MessageEntity via ChatsAndMessages
    var participants by UserEntity via ChatsAndParticipants

    fun mapChat() = NewChatRespondModel(
        id = id.value,
        name = name,
        ownerId = ownerId,
    )
}