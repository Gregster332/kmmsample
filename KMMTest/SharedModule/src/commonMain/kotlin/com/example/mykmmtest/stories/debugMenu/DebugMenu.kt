package com.example.mykmmtest.stories.debugMenu

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.example.core.CoroutineFeature
import com.example.core.Services.SettingsPersistent
import com.example.core.Services.SettingsValue
import com.example.core.koin.ComponentKoinContext
import com.example.core.koin.coreModules
import com.example.corenetwork.di.coreNetworkModule
import com.example.core.MVI.AnyStateFlow
import com.example.mykmmtest.utils.value
import com.example.core.MVI.wrapToAny
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

interface DebugMenu {
    val settings: Value<SettingsSections>
    fun close()
    fun updateSettings(value: SettingsValue, newValue: Any)
}

class DebugMenuComponent(
    componentContext: ComponentContext,
    private val onClose: () -> Unit,
) : DebugMenu, ComponentContext by componentContext {
    private val koinContext =
        instanceKeeper.getOrCreate {
            ComponentKoinContext()
        }

    private val scope =
        koinContext.getOrCreateKoinScope(
            coreModules + listOf(coreNetworkModule),
        )

    private val feature =
        instanceKeeper.getOrCreate {
            DebugMenuFeature(scope.get())
        }

    override val settings = feature.state.value(lifecycle)

    override fun close() {
        onClose()
    }

    override fun updateSettings(value: SettingsValue, newValue: Any) {
        val updated = when(value) {
            is SettingsValue.StringValue -> SettingsValue.StringValue(value.key, newValue as? String ?: value.value)
            is SettingsValue.BoolValue -> SettingsValue.BoolValue(value.key, newValue as? Boolean ?: value.value)
        }
        feature.willChangeSettings(updated) {
            //_settings.value = mapSettings(feature.state.value)
        }
    }
}

internal class DebugMenuFeature(
    private val settingsPersistent: SettingsPersistent,
) : CoroutineFeature() {
    private val _state = MutableStateFlow(SettingsSections())
    val state: AnyStateFlow<SettingsSections> = _state.wrapToAny()

    init {
        observeSettings()
    }

    fun willChangeSettings(value: SettingsValue, callback: () -> Unit) {
        //println(value.value())
        settingsPersistent.add(value).also {
            callback()
        }
    }

    private fun observeSettings() {
        settingsPersistent
            .allSettings
            .onEach {
                _state.emit(mapSettings(it))
            }
            .launchIn(coroutineScope)
    }

    private fun mapSettings(list: List<SettingsValue>): SettingsSections {
        var newList = SettingsSections()
        list.forEach { value ->
            value.let { setting ->
                if (setting is SettingsValue.BoolValue) {
                    //println(setting.key)
                    var new = newList.boolSection.settings.toMutableList()
                    new.add(setting)
                    newList.boolSection.settings = new.toList()
                    newList.boolSection.settings.sortedBy { it.key }
                }
                if (setting is SettingsValue.StringValue) {
                    //println(setting.value)
                    var new = newList.stringsSection.settings.toMutableList()
                    new.add(setting)
                    newList.stringsSection.settings = new.toList()
                    newList.stringsSection.settings.sortedBy { it.key }
                }
            }
        }
        return newList
    }
}

data class SettingsSections(
    var boolSection: BooleanSection = BooleanSection(),
    var stringsSection: StringsSection = StringsSection()
)

data class BooleanSection(
    val title: String = "Toggles",
    var settings: List<SettingsValue.BoolValue> = listOf(),
)

data class StringsSection(
    val title: String = "String",
    var settings: List<SettingsValue.StringValue> = listOf()
)
