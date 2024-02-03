package com.example.mykmmtest.DI

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.mykmmtest.MainDatabase
import org.koin.dsl.module

actual var platformModule = module {
    single<SqlDriver> {
        AndroidSqliteDriver(MainDatabase.Schema, get(), "MainDatabse.db")
    }
}