package com.example.mykmmtest.DI

import com.example.mykmmtest.Services.SecurePersistant.SecurePersistant
import com.example.mykmmtest.Services.SecurePersistant.SecurePersistantImpl
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun settingsModule(): Module = module {
    single<SecurePersistant> {
        SecurePersistantImpl()
    }
}