package com.example.mykmmtest

import com.example.mykmmtest.DI.initKoin
import com.russhwolf.settings.KeychainSettings
import com.russhwolf.settings.Settings
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun initKoinIOS(settings: KeychainSettings) = initKoin(
    appDeclaration = module {  }
)

//module {
//    single<Settings>(named("default_settings")) {
//        settings
//    }
//}