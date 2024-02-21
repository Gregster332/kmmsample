package com.example.backend.db

import com.example.backend.NewChatRequestModel
import com.example.backend.NewChatRespondModel
import com.example.backend.db.entities.ChatEntity
import com.example.backend.db.entities.MessageEntity
import com.example.backend.db.entities.MessageMap
import com.example.backend.db.entities.UserEntity
import com.example.backend.db.tables.Chats
import com.example.backend.db.tables.Messages
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.Table
import java.util.UUID

//@Serializable
//data class ChatsAndMessagesMapEntity(
//    val chat: NewChatRequestModel,
//    val messages: List<MessageMap>,
//)
