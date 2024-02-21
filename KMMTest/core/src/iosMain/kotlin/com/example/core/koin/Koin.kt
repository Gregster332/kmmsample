package com.example.core.koin

import com.example.core.Services.KeyVaultConstants
import com.liftric.kvault.KVault
import org.koin.dsl.module

actual fun keyVault() = module {
        single {
            KVault(
                serviceName = KeyVaultConstants.keyVaultStorageIosServiceName,
            )
        }
    }
