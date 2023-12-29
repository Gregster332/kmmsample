package com.example.mykmmtest.Services.SecurePersistant

import org.koin.core.component.KoinComponent
import com.russhwolf.settings.KeychainSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set

actual class SecurePersistantImpl: SecurePersistant, KoinComponent {

    val settings = KeychainSettings("auth_service")

    override fun getValue(key: String): String? {
        return settings[key]
    }

    override fun set(value: String?, key: String) {
        settings[key] = value
    }

    override fun clear() {
        settings.clear()
    }
}