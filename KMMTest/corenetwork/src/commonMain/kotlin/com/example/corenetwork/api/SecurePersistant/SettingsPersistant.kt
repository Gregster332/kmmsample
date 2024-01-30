package com.example.corenetwork.api.SecurePersistant

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set

//public enum class SettingsKeys {
//    AUTH_TOKEN
//}

public interface SettingsPersistent {
    fun add(key: String, value: String)
    fun getString(byKey: String): Any?
}


internal class SettingsPersistentImpl(
    private val settings: Settings = Settings()
): SettingsPersistent {

    override fun add(key: String, value: String) {
        try {
            settings.putString(key, value)
        } catch (e: Exception) {
            println("Error settings $e")
        }
    }

    override fun getString(byKey: String): String? {
        return settings.getStringOrNull(byKey)
    }
}