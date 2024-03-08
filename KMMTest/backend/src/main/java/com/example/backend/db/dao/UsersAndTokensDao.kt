package com.example.backend.db.dao

import com.example.backend.db.DatabaseSingleton
import com.example.backend.db.entities.UsersAndTokensEntity
import com.example.backend.db.tables.UsersAndTokens
import com.example.backend.models.UsersAndTokensDataModel
import java.util.UUID

object UsersAndTokensDao {
    suspend fun createNewPair(entity: UsersAndTokensDataModel): Unit = DatabaseSingleton.dbQuery {
        UsersAndTokensEntity.find { UsersAndTokens.userId eq entity.userId }.firstOrNull()?.also {
            it.accessToken = entity.accessToken
            it.refreshToken = entity.refreshToken
        } ?: {
            UsersAndTokensEntity.new {
                userId = entity.userId
                accessToken = entity.accessToken
                refreshToken = entity.refreshToken
            }
        }
    }

    suspend fun getUserIdByRefreshToken(token: String): UUID? = DatabaseSingleton.dbQuery {
        return@dbQuery UsersAndTokensEntity
            .find { UsersAndTokens.refreshToken eq token }.firstOrNull()?.userId
    }

    suspend fun nullifyTokens(id: UUID): Unit = DatabaseSingleton.dbQuery {
        UsersAndTokensEntity.find { UsersAndTokens.userId eq id }.firstOrNull()?.also {
            it.accessToken = null
            it.refreshToken = null
        }
    }
}