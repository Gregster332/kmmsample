package com.example.mykmmtest.DI

import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual var platformModule = module {
    single<Settings>(named("default_settings")) {
        SharedPreferencesSettings(get())
    }
}