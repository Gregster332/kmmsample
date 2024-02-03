package com.example.backend.DB

import com.example.backend.NewChatRequestModel
import com.example.backend.UUIDSerializer
import com.example.backend.UserBaseInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.stringLiteral
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

@Serializable
data class UserModel(
    @Serializable(UUIDSerializer::class)
    val id: UUID = UUID.randomUUID(),
    val nickname: String,
    val email: String,
    val password: String,
    val photoUrl: String? = null,
    val bio: String? = null,
)

fun UserBaseInfo.toUserModel() = UserModel(
    id = UUID.fromString(id),
    nickname = nickname,
    email = email,
    password = password,
    photoUrl = photoUrl,
    bio = bio
)

fun UserModel.toWebModel() = UserBaseInfo(
    id = id.toString(),
    nickname = nickname,
    email = email,
    password = password,
    photoUrl = photoUrl,
    bio = bio
)

interface DAOTable<M, I: Comparable<I>> {
   suspend fun create(entity: M): M?
    suspend fun getAll(): List<M>
    suspend fun getBy(id: I): M?
    suspend fun getBy(equals: (Table) -> Op<Boolean>): List<M>
}

object Ussers : UUIDTable("users_main") {
    val nickname = varchar("nickname", 20)
    val email = varchar("email", 30)
    val passcode = varchar("passcode", 30)
    val photoUrl = varchar("photo_url", 40).nullable()
    val bio = text("bio").nullable()
}

class UsserEntity(id: EntityID<UUID>): UUIDEntity(id) {
    companion object : UUIDEntityClass<UsserEntity>(Ussers)
    var nickname by Ussers.nickname
    var email by Ussers.email
    var passcode by Ussers.passcode
    var photoUrl by Ussers.photoUrl
    var bio by Ussers.bio

    fun mapUsers() = UserModel(
        id.value,
        nickname,
        email,
        passcode,
        photoUrl,
        bio
    )
}

class UsersDao {
    suspend fun createUser(entity: UserModel) = DatabaseSingleton.dbQuery {
        UsserEntity.new {
            nickname = entity.nickname
            email = entity.email
            passcode = entity.password
            photoUrl = entity.photoUrl
            bio = entity.bio
        }
    }

    suspend fun getAllUsers() = DatabaseSingleton.dbQuery {
        UsserEntity.all()
    }

    suspend fun getUserBy(id: UUID) = DatabaseSingleton.dbQuery {
        UsserEntity.findById(id)
    }

    suspend fun getUserBy(nickname: String) = DatabaseSingleton.dbQuery {
        UsserEntity.find { Ussers.nickname eq nickname }
    }

    suspend fun updateUser(entity: UserModel) = DatabaseSingleton.dbQuery {
        UsserEntity.findById(entity.id)?.let {
            it.nickname = entity.nickname
            it.email = entity.email
            it.passcode = entity.password
            it.photoUrl = entity.photoUrl
            it.bio = entity.bio
        }
    }

    suspend fun deleteUser(id: UUID) = DatabaseSingleton.dbQuery {
        UsserEntity.findById(id)?.let {
            it.delete()
        }
    }
}