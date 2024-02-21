package com.example.mykmmtest.controllers

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.example.core.CoroutineFeature
import com.example.core.Services.AppThemeEnum
import com.example.core.Services.SettingsKeys
import com.example.core.Services.SettingsPersistent
import com.example.core.Services.SettingsValue
import com.example.core.Services.value
import com.example.core.koin.ComponentKoinContext
import com.example.core.koin.coreModules
import com.example.core.MVI.AnyStateFlow
import com.example.mykmmtest.utils.value
import com.example.core.MVI.wrapToAny
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class AppThemeController(
    componentContext: ComponentContext
): ComponentContext by componentContext, KoinComponent {
    private val koinContext = instanceKeeper.getOrCreate {
        ComponentKoinContext()
    }

    private val scope = koinContext.getOrCreateKoinScope(coreModules)

    private val feature = instanceKeeper.getOrCreate {
        AppThemeControllerFeature(scope.get())
    }

    val appTheme: Value<AppThemeEnum> = feature.appTheme.value(lifecycle)

    private class AppThemeControllerFeature(
        private val settingsPersistent: SettingsPersistent,
    ): CoroutineFeature() {
        private val _appTheme = MutableStateFlow(AppThemeEnum.DEFAULT)
        val appTheme: AnyStateFlow<AppThemeEnum> = _appTheme.wrapToAny()

        init {
            observeAppTheme()
        }

        private fun observeAppTheme() = coroutineScope.launch {
            settingsPersistent
                .allSettings
                .collect { list ->
                    val appTheme = list.first {
                        it is SettingsValue.StringValue && it.key == SettingsKeys.currentAppTheme
                    }.value() as? String ?: AppThemeEnum.DEFAULT.name
                    _appTheme.value = AppThemeEnum.valueOf(appTheme)
                }
        }
    }
}