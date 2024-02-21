package com.example.backend.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object Messages : IntIdTable() {
    val message_text = text("message_text")
    val senderId = uuid("sender_id")
}