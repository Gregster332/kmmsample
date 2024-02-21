package com.example.core.koin

import com.example.core.Services.KeyVaultStorage
import com.example.core.Services.KeyVaultStorageImpl
import com.example.core.Services.SettingsPersistent
import com.example.core.Services.SettingsPersistentImpl
import org.koin.core.module.Module
import org.koin.dsl.module

val coreModules: List<Module> =
    listOf(
        //keyVault(),
        module {
            single<KeyVaultStorage> {
                KeyVaultStorageImpl()
            }

            single<SettingsPersistent> {
                SettingsPersistentImpl()
            }
        },
    )

expect fun keyVault(): Module
