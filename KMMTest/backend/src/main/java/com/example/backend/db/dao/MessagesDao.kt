package com.example.backend.db.dao

import com.example.backend.db.DatabaseSingleton
import com.example.backend.db.entities.MessageEntity
import com.example.backend.db.entities.MessageMap
import com.example.backend.db.tables.Messages
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.Table

class MessagesDao {
    suspend fun create(entity: MessageMap) = DatabaseSingleton.dbQuery {
        MessageEntity.new {
            message_text = entity.messageText
            senderId = entity.senderId
        }
    }

//    suspend fun getAll() = DatabaseSingleton.dbQuery {
//        MessageEntity.all().map { it.toMessage() }
//    }
//
//    suspend fun getBy(id: Int) = DatabaseSingleton.dbQuery {
//        MessageEntity.findById(id)?.toMessage()
//    }
//
//    suspend fun getBy(equals: (Table) -> Op<Boolean>) = DatabaseSingleton.dbQuery {
//        MessageEntity.find { equals(Messages) }.map { it.toMessage() }
//    }
}