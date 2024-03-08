package com.example.backend.db

import com.example.backend.db.tables.Chats
import com.example.backend.db.tables.ChatsAndMessages
import com.example.backend.db.tables.ChatsAndParticipants
import com.example.backend.db.tables.Messages
import com.example.backend.db.tables.Users
import com.example.backend.db.tables.UsersAndTokens
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseSingleton {
    fun init() {
        val driverClassName = "org.postgresql.Driver"
        val jdbcURL = "jdbc:postgresql://localhost:5432/"
        Database.connect(jdbcURL, driverClassName, user = "postgres", password = "postgres")

        transaction {
            SchemaUtils.createMissingTablesAndColumns(Users, UsersAndTokens, Chats, ChatsAndParticipants, Messages, ChatsAndMessages)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T = newSuspendedTransaction(Dispatchers.IO) { block() }
}
