package com.example.backend.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object ChatsAndParticipants : IntIdTable() {
    val chat = reference("chats", Chats)
    val participants = reference("participants", Users)
}