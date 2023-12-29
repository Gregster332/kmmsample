package com.example.mykmmtest.Services.SecurePersistant

import android.content.Context
import android.preference.PreferenceManager
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import org.koin.core.component.KoinComponent
import java.util.prefs.Preferences

actual class SecurePersistantImpl constructor(
    private val androidContext: Context,
): SecurePersistant, KoinComponent {

    val settings: SharedPreferencesSettings

    init {
        val config = PreferenceManager.getDefaultSharedPreferences(androidContext)
        settings = SharedPreferencesSettings(config)
    }

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