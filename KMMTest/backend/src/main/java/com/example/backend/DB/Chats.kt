package com.example.backend.DB

import com.example.backend.NewChatRequestModel
import com.example.backend.UUIDSerializer
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
import java.lang.NullPointerException
import java.util.UUID

object Messages: IntIdTable() {
    val message_text = text("message_text")
    //val chat = uuid("chat_id").references(Chats.id)
}

class MessageEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<MessageEntity>(Messages)
    var message_text by Messages.message_text
    //var chat by ChatEntity referencedOn ChatsAndMessages.chat

    fun toMessage() = MessageMap(
        messageText = message_text,
        //chatId = chat.id.value
    )
}

@Serializable
data class MessageMap(
    val messageText: String,
//    @Serializable(UUIDSerializer::class)
//    val chatId: UUID
)

object ChatsAndMessages: IntIdTable() {
    val chat = reference("chats", Chats)
    val messages = reference("messages", Messages).nullable()
    val participants = reference("participants", Ussers).nullable()
}

@Serializable
data class ChatsAndMessagesMapEntity(
    val chat: NewChatRequestModel,
    val messages: List<MessageMap>
)

//class ChatsAndMessagesEntity(id: EntityID<Int>): IntEntity(id) {
//    companion object : IntEntityClass<ChatsAndMessagesEntity>(ChatsAndMessages)
//    var chat by ChatEntity referencedOn ChatsAndMessages.chat
//    val messages by MessageEntity referrersOn ChatsAndMessages.messages
//
//    fun map() = ChatsAndMessagesMapEntity(
//        chat = chat.mapChat(),
//        messages = messages.toList().map { it.toMessage() }
//    )
//}

object Chats : UUIDTable("chats") {
    val name = varchar("name", 30)
    val ownerId = uuid("owner_id").references(Ussers.id)
}

class ChatEntity(id: EntityID<UUID>): UUIDEntity(id) {
    companion object : UUIDEntityClass<ChatEntity>(Chats)
    var name by Chats.name
    var ownerId by Chats.ownerId
    var messages by MessageEntity via ChatsAndMessages
    var participants by UsserEntity via ChatsAndMessages

    fun mapChat() = NewChatRequestModel(
        name = name,
        ownerId = ownerId
    )
}
//object  ggg: IntIdTable()
object Ussers : UUIDTable("users_main") {
    val nickname = varchar("nickname", 20)
    val email = varchar("email", 30)
    val passcode = varchar("passcode", 30)
    val photoUrl = varchar("photo_url", 40).nullable()
    val bio = text("bio").nullable()
    //val chat = reference("chat", Chats).nullable()
}


class UsserEntity(id: EntityID<UUID>): UUIDEntity(id) {
    companion object : UUIDEntityClass<UsserEntity>(Ussers)
    var nickname by Ussers.nickname
    var email by Ussers.email
    var passcode by Ussers.passcode
    var photoUrl by Ussers.photoUrl
    var bio by Ussers.bio
    var chats by ChatEntity via ChatsAndMessages

    fun mapUsers() = UserModel(
        id.value,
        nickname,
        email,
        passcode,
        photoUrl,
        bio
    )
}

class ChatsDao: DAOTable<NewChatRequestModel, UUID> {
    override suspend fun create(entity: NewChatRequestModel) = DatabaseSingleton.dbQuery {
//        val message = MessageEntity.new {
//            message_text = "Hello"
//        }
        ChatEntity.new {
            name = entity.name
            ownerId = entity.ownerId
            messages = SizedCollection(listOf())
            participants = SizedCollection(listOf())
        }.mapChat()
    }

    override suspend fun getAll() = DatabaseSingleton.dbQuery {
        ChatEntity.all().map { it.mapChat() }
    }

    override suspend fun getBy(id: UUID) = DatabaseSingleton.dbQuery {
        ChatEntity.find { Chats.id eq id }.map { it.mapChat() }.firstOrNull()
    }

    override suspend fun getBy(equals: (Table) -> Op<Boolean>) = DatabaseSingleton.dbQuery {
        ChatEntity.find { equals(Chats) }.map { it.mapChat() }
    }
}

class UsersDao: DAOTable<UserModel, UUID> {
    override suspend fun create(entity: UserModel) = DatabaseSingleton.dbQuery {
        UsserEntity.new {
            nickname = entity.nickname
            email = entity.email
            passcode = entity.password
            photoUrl = entity.photoUrl
            bio = entity.bio
            chats = SizedCollection(emptyList())
        }.mapUsers()
    }

    override suspend fun getAll()  = DatabaseSingleton.dbQuery {
        UsserEntity.all().map { it.mapUsers() }
    }
    override suspend fun getBy(id: UUID) = DatabaseSingleton.dbQuery {
        UsserEntity.findById(id)?.mapUsers()
    }

    override suspend fun getBy(equals: (Table) -> Op<Boolean>) = DatabaseSingleton.dbQuery {
        UsserEntity.find { equals(Ussers) }.map { it.mapUsers() }
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