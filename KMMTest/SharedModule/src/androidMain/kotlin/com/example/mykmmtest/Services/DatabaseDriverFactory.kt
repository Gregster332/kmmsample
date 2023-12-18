package com.example.mykmmtest.Services

import android.content.Context
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

//actual class DatabaseDriverFactory(private val context: Context) {
//    actual fun createDriver(schema: SqlSchema<QueryResult.Value<Unit>>): SqlDriver {
//        return AndroidSqliteDriver(schema = schema, context = context,"test.db")
//    }
//}