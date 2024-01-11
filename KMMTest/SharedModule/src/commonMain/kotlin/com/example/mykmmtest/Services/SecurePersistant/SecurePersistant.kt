package com.example.mykmmtest.Services.SecurePersistant

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import org.koin.core.component.KoinComponent

interface SecurePersistant {
    fun getValue(key: String): String?
    fun set(value: String?, key: String)
    fun clear()
}

internal class SecurePersistantImpl(
    private var settings: Settings
): SecurePersistant, KoinComponent {
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

