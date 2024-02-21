package com.example.backend.db.dao

import com.example.backend.UserBaseInfo
import com.example.backend.db.DatabaseSingleton
import com.example.backend.db.entities.UserEntity
import com.example.backend.db.tables.Users
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.UUID

class UsersDao {
    suspend fun createUser(entity: UserBaseInfo) = DatabaseSingleton.dbQuery {
        UserEntity.new {
            nickname = entity.nickname
            email = entity.email
            passcode = entity.password
            photoUrl = entity.photoUrl
            bio = entity.bio
        }
    }

    suspend fun getAllUsers() = DatabaseSingleton.dbQuery {
        UserEntity.all()
    }

    suspend fun getUserBy(id: UUID) = DatabaseSingleton.dbQuery {
        UserEntity.findById(id)
    }

    suspend fun getUserBy(nickname: String) = DatabaseSingleton.dbQuery {
        UserEntity.find { Users.nickname eq nickname }
    }

    suspend fun updateUser(entity: UserBaseInfo) = DatabaseSingleton.dbQuery {
        UserEntity.findById(entity.id)?.let {
            it.nickname = entity.nickname
            it.email = entity.email
            it.passcode = entity.password
            it.photoUrl = entity.photoUrl
            it.bio = entity.bio
        }
    }

    suspend fun deleteUser(id: UUID) = DatabaseSingleton.dbQuery {
        UserEntity.findById(id)?.let {
            it.delete()
        }
    }
}