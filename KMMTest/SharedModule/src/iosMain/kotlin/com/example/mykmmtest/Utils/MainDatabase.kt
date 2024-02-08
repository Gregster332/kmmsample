package com.example.mykmmtest.Utils

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.example.mykmmtest.MainDatabase

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(MainDatabase.Schema, "MainDatabase.db")
    }
}