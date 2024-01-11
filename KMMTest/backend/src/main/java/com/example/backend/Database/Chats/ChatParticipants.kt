package com.example.backend.Database.Chats

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object Users : Table("users") {
    private val id = varchar("id", 50)
    private val name = varchar("name", 20)
    private val password = varchar("password", 20)

    fun insert(userDTO: UserDTO) {
        transaction {
            try {
                Users.insert {
                    it[id] = userDTO.id
                    it[name] = userDTO.name
                    it[password] = userDTO.password
                }
            } catch (e: ExposedSQLException) {
                println(e)
            }
        }
    }

    fun fetchUser(id: String): UserDTO? {
        try {
            val userModel = Users.select { Users.id.eq(id) }.single()
            return UserDTO(
                id = userModel[Users.id],
                name = userModel[Users.name],
                password = userModel[Users.password]
            )
        } catch (e: Exception) {
            return null
        }
    }
}

class UserDTO(
    val id: String,
    val name: String,
    val password: String
)

object Chat: Table("chat") {
    private val id = integer("id").autoIncrement()
    private val name = varchar("name", 30)
    private val mesages = charArrayOf()

//    fun get(): List<ChatDTO> {
//        return try {
//        transaction {
//            Chat.selectAll().toList()
//                    .map {
//                        ChatDTO(
//                            it[mesages]
//                            id = it[id],
//                            name = it[name],
//                            mesages = it[mesages]
//                        )
//                    }
//            }
//        } catch(e: ExposedSQLException) {
//            println(e)
//            return emptyList()
//        }
//    }
}

class ChatDTO(
    val id: Int,
    val name: String,
    val mesages: Array<String>
)

class UsersController(private val call: ApplicationCall) {
    suspend fun registerNewUser(id: String) {
        val isUserExist = Users.fetchUser(id) != null

        if (isUserExist) {
            call.respond(HttpStatusCode.Conflict, "User has already exists")
        } else {
            Users.insert(UserDTO(id, "Greg", "pass"))
        }
        call.respond(HttpStatusCode.OK, "New user created")
    }

    suspend fun getUser(id: String) {
        //Users.select()
    }
}