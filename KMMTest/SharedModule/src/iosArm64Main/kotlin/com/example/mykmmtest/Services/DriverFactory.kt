package com.example.mykmmtest.Services

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.example.app.core.database.CoreDB


actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(CoreDB.Schema, "test.db")
    }
}