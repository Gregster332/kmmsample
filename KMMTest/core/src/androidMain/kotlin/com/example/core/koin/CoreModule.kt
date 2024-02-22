package com.example.core.koin

import com.liftric.kvault.KVault
import org.koin.dsl.module
import org.koin.dsl.single

actual fun keyVault() = module {
    single {
        KVault(context = get(), "key_vault_android")
    }
}
