package com.example.mykmmtest.di

import app.cash.sqldelight.db.SqlDriver
import com.example.mykmmtest.utils.DriverFactory
import org.koin.dsl.module

actual var platformModule =
    module {
        single<SqlDriver> { DriverFactory().createDriver() }
    }
