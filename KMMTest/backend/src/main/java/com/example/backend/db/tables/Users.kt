package com.example.backend.db.tables

import org.jetbrains.exposed.dao.id.UUIDTable

object Users : UUIDTable("users_main") {
    val nickname = varchar("nickname", 20)
    val email = varchar("email", 30)
    val passcode = varchar("passcode", 30)
    val photoUrl = varchar("photo_url", 40).nullable()
    val bio = text("bio").nullable()
}