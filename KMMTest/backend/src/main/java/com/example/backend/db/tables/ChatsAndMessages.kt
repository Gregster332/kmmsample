package com.example.backend.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object ChatsAndMessages : IntIdTable() {
    val chat = reference("chats", Chats)
    val messages = reference("messages", Messages).nullable()
}