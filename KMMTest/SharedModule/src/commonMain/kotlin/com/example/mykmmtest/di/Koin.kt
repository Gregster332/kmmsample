package com.example.mykmmtest.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.logging.logger.Logger
import com.arkivanov.mvikotlin.logging.store.LoggingStoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.example.core.koin.ComponentKoinContext
import com.example.core.koin.coreModules
import com.example.core.koin.keyVault
import com.example.corenetwork.api.auth.LocalCache
import com.example.corenetwork.api.devicesensorsapi.DeviceSensorApi
import com.example.corenetwork.di.coreNetworkModule
import com.example.mykmmtest.MainDatabase
import com.example.mykmmtest.utils.LocalCacheImpl
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.QualifierValue
import org.koin.core.qualifier.named
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

internal lateinit var deviceSensor: DeviceSensorApi

fun initKoin(
    block: KoinAppDeclaration = {},
    deviceSensorApi: DeviceSensorApi
) {
    deviceSensor = deviceSensorApi
    startKoin {
        block()
        keyVault()
        modules(coreModules)
        modules(
            platformModule,
            coreNetworkModule,
            storeFactoryModule,
            coreBD,
            platformDependenciesModule(),
            services(),
        )
    }
}

internal var coreBD =
    module {
        single {
            MainDatabase(get())
        }
    }

internal var storeFactoryModule =
    module {
        factory<StoreFactory> {
            val logger =
                object : Logger {
                    override fun log(text: String) {
                        println(text)
                    }
                }
            LoggingStoreFactory(DefaultStoreFactory(), logger = logger)
        }
    }

fun services() = module {
    single<LocalCache> {
        LocalCacheImpl()
    }
}

expect var platformModule: Module

fun platformDependenciesModule() = module {
    single<DeviceSensorApi> {
        deviceSensor
    }
}