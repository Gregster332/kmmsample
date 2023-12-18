package com.example.mykmmtest.Services

import app.cash.sqldelight.TransactionWithReturn
import app.cash.sqldelight.TransactionWithoutReturn
import app.cash.sqldelight.db.SqlDriver
import com.example.app.core.database.CoreDB
import comexampleappcoredatabase.CoreDBQueries
import comexampleappcoredatabase.HockeyPlayer
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow

expect class DriverFactory {
    fun createDriver(): SqlDriver
}

internal class DatabaseManager(driverFactory: DriverFactory): CoreDB {
    private val driver = driverFactory.createDriver()
    private val database = CoreDB(driver)

    override val coreDBQueries: CoreDBQueries = database.coreDBQueries

    fun getAllPlayers(): List<HockeyPlayer> {
        return coreDBQueries.selectAll().executeAsList()
    }

    fun insertPlayer(payerNumber: Long, name: String) {
        coreDBQueries.insertIn(payerNumber, name)
    }

    fun clearAll() {
        coreDBQueries.transaction {
            coreDBQueries.removeAllPlayers()
        }
    }

    override fun transaction(noEnclosing: Boolean, body: TransactionWithoutReturn.() -> Unit) {
        print("Some11111111")
        TODO("Not yet implemented")
    }

    override fun <R> transactionWithResult(
        noEnclosing: Boolean,
        bodyWithReturn: TransactionWithReturn<R>.() -> R
    ): R {
        print("Some2222222")
        TODO("Not yet implemented")
    }
}
