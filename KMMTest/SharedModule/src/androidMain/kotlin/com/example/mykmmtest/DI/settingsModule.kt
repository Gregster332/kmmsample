package com.example.mykmmtest.DI

import android.content.SharedPreferences
import com.example.mykmmtest.Services.SecurePersistant.SecurePersistant
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.core.qualifier.named
import org.koin.core.qualifier.qualifier
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
