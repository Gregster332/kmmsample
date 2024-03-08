package com.example.backend.db.tables

import com.example.backend.db.DatabaseSingleton
import com.example.backend.db.entities.MessageEntity
import com.example.backend.models.UsersAndTokensDataModel
import com.example.backend.utils.serializers.UUIDSerializer
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import java.util.UUID

object UsersAndTokens : IntIdTable() {
    val userId = uuid("user_id")
    val accessToken = text("access_token").nullable()
    val refreshToken = text("refresh_token").nullable()
}