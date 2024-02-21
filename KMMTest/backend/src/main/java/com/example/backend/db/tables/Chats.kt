package com.example.backend.db.tables

import org.jetbrains.exposed.dao.id.UUIDTable

object Chats : UUIDTable("chats") {
    val name = varchar("name", 30)
    val ownerId = uuid("owner_id").references(Users.id)
}