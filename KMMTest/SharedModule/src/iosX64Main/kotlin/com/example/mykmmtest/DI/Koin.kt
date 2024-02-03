package com.example.mykmmtest.DI

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.example.mykmmtest.Utils.DriverFactory
import org.koin.dsl.module

actual var platformModule = module {
    single <SqlDriver> { DriverFactory().createDriver() }
}