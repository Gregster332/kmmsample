package com.example.backend.db.dao

import com.example.backend.NewChatRequestModel
import com.example.backend.NewChatRespondModel
import com.example.backend.db.DatabaseSingleton
import com.example.backend.db.entities.ChatEntity
import com.example.backend.db.entities.UserEntity
import com.example.backend.db.tables.Chats
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.Table
import java.util.UUID

class ChatsDao {
    suspend fun create(entity: NewChatRequestModel, initialUsers: List<UserEntity> = emptyList()) =
        DatabaseSingleton.dbQuery {
            ChatEntity.new {
                name = entity.name
                ownerId = entity.ownerId
                messages = SizedCollection(listOf())
                participants = SizedCollection(initialUsers)
            }.mapChat()
        }

    suspend fun getAll() = DatabaseSingleton.dbQuery {
        ChatEntity.all().map { it.mapChat() }
    }

    suspend fun getBy(id: UUID) = DatabaseSingleton.dbQuery {
        ChatEntity.find { Chats.id eq id }.firstOrNull()
    }

    suspend fun getBy(equals: (Table) -> Op<Boolean>) = DatabaseSingleton.dbQuery {
        ChatEntity.find { equals(Chats) }.map { it.mapChat() }
    }

    suspend fun getChatsBy(userId: UUID): List<NewChatRespondModel> = DatabaseSingleton.dbQuery {
        val user = UserEntity.findById(userId)?.mapRequestModel() ?: return@dbQuery emptyList()

        return@dbQuery ChatEntity.all().mapNotNull { entity ->
            if (entity.participants.toList().map { it.mapRequestModel() }.contains(user)) {
                entity
            } else {
                null
            }
        }.map { it.mapChat() }
    }
}