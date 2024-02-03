package com.example.mykmmtest.DI

import app.cash.sqldelight.db.SqlDriver
import com.example.mykmmtest.Utils.DriverFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual var platformModule = module {
    single <SqlDriver> { DriverFactory().createDriver() }
}