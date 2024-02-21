package com.example.backend.db.entities

import com.example.backend.UserBaseInfo
import com.example.backend.db.tables.Users
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

class UserEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<UserEntity>(Users)
    var nickname by Users.nickname
    var email by Users.email
    var passcode by Users.passcode
    var photoUrl by Users.photoUrl
    var bio by Users.bio

    fun mapRequestModel() = UserBaseInfo(
        id.value,
        nickname,
        email,
        passcode,
        photoUrl,
        bio
    )
}