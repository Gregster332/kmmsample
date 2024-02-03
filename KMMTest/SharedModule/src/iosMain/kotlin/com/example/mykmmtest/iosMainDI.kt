package com.example.mykmmtest

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.example.mykmmtest.DI.initKoin
import com.example.mykmmtest.Utils.DriverFactory
import com.russhwolf.settings.KeychainSettings
import com.russhwolf.settings.Settings
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun initKoinIOS(settings: KeychainSettings) = initKoin(
    appDeclaration = module {
        single <SqlDriver> { DriverFactory().createDriver() }
        //single  { NativeSqliteDriver(MainDatabase.Schema, "MainDatabse.db") }
    }
)

//module {
//    single<Settings>(named("default_settings")) {
//        settings
//    }
//}