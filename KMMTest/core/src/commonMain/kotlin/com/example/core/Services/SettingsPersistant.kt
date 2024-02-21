package com.example.core.Services

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppThemeEnum(name: String) {
    DEFAULT("DEFAULT"),
    LIGHT("LIGHT"),
    DARK("DARK")
}

object SettingsKeys {
    val skipAuthKey = KeyNamingFactory.createKey("skip_auth")
    val fakeChats = KeyNamingFactory.createKey("fake_chats")
    val currentAppTheme = KeyNamingFactory.createKey("current_app_theme")
}

sealed interface Listener<Type> {
    data class StringListener(val key: String, val callback: (String) -> Unit) : Listener<String>
    data class BoolListener(val key: String, val callback: (Boolean) -> Unit) : Listener<Boolean>
}

sealed interface SettingsValue {
    data class StringValue(val key: String, val value: String = "") : SettingsValue
    data class BoolValue(val key: String, val value: Boolean = false) : SettingsValue

    companion object {
        fun of(key: String, string: String) = StringValue(key, string)
        fun of(key: String, bool: Boolean) = BoolValue(key, bool)
    }
}

fun SettingsValue.value(): Any? = when(this) {
    is SettingsValue.StringValue -> { this.value }
    is SettingsValue.BoolValue -> { this.value }
}

interface SettingsPersistent {
    val allSettings: StateFlow<List<SettingsValue>>
    fun add(type: SettingsValue)
    fun get(type: SettingsValue): SettingsValue
    fun <Type> listen(listener: Listener<Type>)
    fun clear()
}

internal class SettingsPersistentImpl(
    private val settings: Settings = Settings(),
) : SettingsPersistent {
    private val _allSettings: MutableStateFlow<List<SettingsValue>> = MutableStateFlow(emptyList())
    override val allSettings: StateFlow<List<SettingsValue>> = _allSettings.asStateFlow()

    private val initialSettings: List<SettingsValue> = listOf(
        SettingsValue.BoolValue(SettingsKeys.skipAuthKey, false),
        SettingsValue.BoolValue(SettingsKeys.fakeChats, false),
        SettingsValue.StringValue(SettingsKeys.currentAppTheme, AppThemeEnum.DEFAULT.name)
    )

    init {
        initialSettings.forEach {
            when (it) {
                is SettingsValue.BoolValue -> {
                    if (settings.getBooleanOrNull(it.key) == null) {
                        add(it)
                    }
                    return@forEach
                }
                is SettingsValue.StringValue -> {
                    if (settings.getStringOrNull(it.key) == null) {
                        add(it)
                    }
                    return@forEach
                }
            }
        }
        getAllSettings()
    }

    @Suppress("UNCHECKED_CAST")
    override fun get(type: SettingsValue): SettingsValue = when (type) {
        is SettingsValue.StringValue -> {
            val value = settings.getString(KeyNamingFactory.createKey(type.key), "")
            SettingsValue.StringValue(type.key, value)
        }
        is SettingsValue.BoolValue -> {
            val value = settings.getBoolean(KeyNamingFactory.createKey(type.key),false)
            SettingsValue.BoolValue(type.key, value)
        }
    }

    override fun add(type: SettingsValue) = when (type) {
        is SettingsValue.StringValue -> {
            settings.putString(KeyNamingFactory.createKey(type.key), type.value)
            getAllSettings()
        }
        is SettingsValue.BoolValue -> {
            settings.putBoolean(KeyNamingFactory.createKey(type.key), type.value)
            getAllSettings()
        }
    }

    override fun <Type> listen(listener: Listener<Type>) = when (listener) {
        is Listener.StringListener -> {
            val string = settings.getString(KeyNamingFactory.createKey(listener.key), "")
            listener.callback(string)
        }
        is Listener.BoolListener -> {
            val bool = settings.getBoolean(KeyNamingFactory.createKey(listener.key), false)
            listener.callback(bool)
        }
    }

    override fun clear() {
        settings.keys.filter { it.contains(KeyNamingFactory.prefix) }.forEach {
            settings.remove(it)
        }
    }

    private fun getAllSettings() {
        val allSettings: MutableList<SettingsValue> = mutableListOf()

        initialSettings.forEach {
            when (it) {
                is SettingsValue.BoolValue -> {
                    val value = settings.getBoolean(it.key, false)
                    allSettings.add(SettingsValue.BoolValue(it.key, value))
                }
                is SettingsValue.StringValue -> {
                    val value = settings.getString(it.key, "")
                    allSettings.add(SettingsValue.StringValue(it.key, value))
                }
            }
        }
        this._allSettings.value = allSettings
    }
}

internal object KeyNamingFactory {
    const val prefix = "superchat_"

    fun createKey(base: String): String = if (!base.contains(prefix)) prefix + base else base
}
