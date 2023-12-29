package com.example.mykmmtest.Services.SecurePersistant

import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.KeychainSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import org.koin.core.component.KoinComponent

actual class SecurePersistantImpl: SecurePersistant, KoinComponent {

    val settings = KeychainSettings("auth_service")

    @OptIn(ExperimentalSettingsImplementation::class)
    override fun getValue(key: String): String? {
        return settings.get(key, defaultValue = "")
    }

    override fun set(value: String?, key: String) {
        settings[key] = value
    }

    override fun clear() {
        settings.clear()
    }
}