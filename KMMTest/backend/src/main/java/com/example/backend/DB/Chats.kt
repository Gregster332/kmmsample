package com.example.backend.DB

import com.example.backend.NewChatRequestModel
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.Table
import java.util.UUID

object Messages: IntIdTable() {
    val message_text = text("message_text")
}

class MessageEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<MessageEntity>(Messages)
    var message_text by Messages.message_text

    fun toMessage() = MessageMap(
        messageText = message_text,
    )
}

@Serializable
data class MessageMap(
    val messageText: String,
)

object ChatsAndMessages: IntIdTable() {
    val chat = reference("chats", Chats)
    val messages = reference("messages", Messages).nullable()
}

object ChatsAndParticipants: IntIdTable() {
    val chat = reference("chats", Chats)
    val participants = reference("participants", Ussers)
}

@Serializable
data class ChatsAndMessagesMapEntity(
    val chat: NewChatRequestModel,
    val messages: List<MessageMap>
)

object Chats : UUIDTable("chats") {
    val name = varchar("name", 30)
    val ownerId = uuid("owner_id").references(Ussers.id)
}

class ChatEntity(id: EntityID<UUID>): UUIDEntity(id) {
    companion object : UUIDEntityClass<ChatEntity>(Chats)
    var name by Chats.name
    var ownerId by Chats.ownerId
    var messages by MessageEntity via ChatsAndMessages
    var participants by UsserEntity via ChatsAndParticipants

    fun mapChat() = NewChatRequestModel(
        name = name,
        ownerId = ownerId
    )
}

class ChatsDao {
    suspend fun create(
        entity: NewChatRequestModel,
        initialUsers: List<UsserEntity> = emptyList()
    ) = DatabaseSingleton.dbQuery {
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
        ChatEntity.find { Chats.id eq id }.map { it.mapChat() }.firstOrNull()
    }

    suspend fun getBy(equals: (Table) -> Op<Boolean>) = DatabaseSingleton.dbQuery {
        ChatEntity.find { equals(Chats) }.map { it.mapChat() }
    }

    suspend fun getChatsBy(userId: UUID): List<NewChatRequestModel> = DatabaseSingleton.dbQuery {
        val user = UsserEntity.findById(userId)?.mapUsers() ?: return@dbQuery emptyList()

        return@dbQuery ChatEntity.all().mapNotNull { entity ->
            if (entity.participants.toList().map { it.mapUsers() }.contains(user)) {
                entity
            } else {
                null
            }
        }.map { it.mapChat() }
    }
}

class MessagesDao: DAOTable<MessageMap, Int> {
    override suspend fun create(entity: MessageMap) = DatabaseSingleton.dbQuery {
        MessageEntity.new {
            message_text = entity.messageText
        }.toMessage()
    }

    override suspend fun getAll() = DatabaseSingleton.dbQuery {
        MessageEntity.all().map { it.toMessage() }
    }

    override suspend fun getBy(id: Int) = DatabaseSingleton.dbQuery {
        MessageEntity.findById(id)?.toMessage()
    }

    override suspend fun getBy(equals: (Table) -> Op<Boolean>) = DatabaseSingleton.dbQuery {
        MessageEntity.find { equals(Messages) }.map { it.toMessage() }
    }
}