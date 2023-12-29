package com.example.backend.Database

import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class UserData(val name: String, val surname: String)

object UserDataDB : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 10)
    val surname = varchar("surname", 20)

    override val primaryKey = PrimaryKey(id)
}

object DatabaseService {
    fun init() {
        val driverClassName = "org.h2.Driver"
        val jdbcURL = "jdbc:h2:file:./build/db"
        val database = Database.connect(jdbcURL, driverClassName)
        transaction(database) {
            SchemaUtils.create(UserDataDB)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}

class DAOService {

    private fun resultRowToArticle(row: ResultRow) = UserData(
        name = row[UserDataDB.name],
        surname = row[UserDataDB.surname]
    )

    suspend fun appendNewUser(name: String, surname: String): Unit = DatabaseService.dbQuery {
        val statement = UserDataDB.insert {
            it[UserDataDB.name] = name
            it[UserDataDB.surname] = surname
        }
        statement.resultedValues?.singleOrNull()?.let(::resultRowToArticle)
    }

    suspend fun getAllUsers(): List<UserData> = DatabaseService.dbQuery {
        UserDataDB.selectAll().map(::resultRowToArticle)
    }

    suspend fun deleteAll() {
        newSuspendedTransaction {
            UserDataDB.deleteWhere { UserDataDB.name eq "Gerg" }
        }
    }
}
