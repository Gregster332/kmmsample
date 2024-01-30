package com.example.backend.DB

import com.example.backend.NewChatRequestModel
import com.example.backend.UserBaseInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
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
    @Contextual val id: UUID = UUID.randomUUID(),
    val nickname: String,
    val email: String,
    val password: String,
    val photoUrl: String? = null,
    val bio: String? = null,
    //val chats: List<NewChatRequestModel> = emptyList()
)

fun UserBaseInfo.toUserModel() = UserModel(
    id = UUID.fromString(id),
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

//object User : Table("users") {
//    val id = text("id").uniqueIndex()
//    val nickname = varchar("nickname", 20)
//    val email = varchar("email", 30)
//    val password = varchar("password", 30)
//    val photoUrl = varchar("photo_url", 40).nullable()
//    val bio = text("bio").nullable()
//    val chats = reference("chats", Chats()).nullable()
//
//    fun toUserModel(row: ResultRow) = UserModel(
//        id = row[User.id],
//        nickname = row[User.nickname],
//        email = row[User.email],
//        password = row[User.password],
//        photoUrl = row[User.photoUrl],
//        bio = row[User.bio]
//    )
//}

//class UserDaoImpl: DAOTable<UserModel> {
//    override fun create(entity: UserModel) {
//        try {
//            User.insert {
//                it[id] = entity.id
//                it[nickname] = entity.nickname
//                it[email] = entity.email
//                it[password] = entity.password
//                it[photoUrl] = entity.photoUrl
//                it[bio] = entity.bio
//            }
//        } catch (e: Exception) {
//            println("DB ERROR User insert: $e")
//        }
//    }
//
//    override fun getAll(): List<UserModel> = User.selectAll().map(User::toUserModel)
//    override fun getBy(equals: (Table) -> Op<Boolean>) = User.select { equals(User) }.map(User::toUserModel).firstOrNull()
//
////    override fun insertAndGetId(entity: UserModel): Any? {
////        User.inser
////    }
//}

object DatabaseSingleton {
    fun init() {
        val driverClassName = "org.postgresql.Driver"
        val jdbcURL = "jdbc:postgresql://localhost:5432/"
        Database.connect(jdbcURL, driverClassName, user = "postgres", password = "postgres")

        transaction {
            SchemaUtils.createMissingTablesAndColumns(Ussers, Chats, Messages, ChatsAndMessages)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}

//interface DAOFacade<M> {
//    suspend fun all(): List<M>
//    suspend fun getById(): M?
//    suspend fun insert(entity: M)
//}

//abstract class BaseDatabaseRepository<T>(
//    private val dao: DAOTable<T>
//) {
//    suspend fun create(entity: T) = withContext(Dispatchers.IO) {
//        dao.create(entity)
//    }
//
//    suspend fun getAll(): List<T> = withContext(Dispatchers.IO) {
//        dao.getAll()
//    }
//
//    suspend fun getBy(equals: (Table) -> Op<Boolean>): T? = withContext(Dispatchers.IO) {
//        dao.getBy { equals(it) }
//    }
//}
