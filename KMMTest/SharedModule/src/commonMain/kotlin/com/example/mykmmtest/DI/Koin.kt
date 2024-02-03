package com.example.mykmmtest.DI

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.logging.logger.Logger
import com.arkivanov.mvikotlin.logging.store.LoggingStoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.example.corenetwork.api.Auth.LocalCache
import com.example.corenetwork.api.Chats.WebSocketService
import com.example.corenetwork.api.SecurePersistant.SettingsPersistent
import com.example.mykmmtest.MainDatabase
import com.example.mykmmtest.Services.WebSocketServiceImpl
import com.example.mykmmtest.Utils.LocalCacheImpl
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(
    block: KoinAppDeclaration = {},
    appDeclaration: Module
) {
    startKoin {
        block()
        modules(
            appDeclaration,
            platformModule,
            coreBD,
            services()
        )
    }
}

internal var coreBD = module {
    single {
        MainDatabase(get())
    }
}

internal var storeFactoryModule = module {
    factory<StoreFactory> {
        val logger = object : Logger {
            override fun log(text: String) {
                println(text)
            }
        }
        LoggingStoreFactory(DefaultStoreFactory(), logger = logger)
    }
}

fun services(
) = module {
    factory<WebSocketService> {
        WebSocketServiceImpl()
    }

    single<LocalCache> {
        LocalCacheImpl()
    }
}

expect var platformModule: Module
