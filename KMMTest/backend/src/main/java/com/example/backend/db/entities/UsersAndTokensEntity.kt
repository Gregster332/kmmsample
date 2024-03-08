package com.example.backend.db.entities

import com.example.backend.db.tables.UsersAndTokens
import com.example.backend.models.UsersAndTokensDataModel
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class UsersAndTokensEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<UsersAndTokensEntity>(UsersAndTokens)

    var userId by UsersAndTokens.userId
    var accessToken by UsersAndTokens.accessToken
    var refreshToken by UsersAndTokens.refreshToken

    fun dataModel() = UsersAndTokensDataModel(
        userId,
        accessToken,
        refreshToken
    )
}