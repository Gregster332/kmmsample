package com.example.apptheme

import com.arkivanov.decompose.value.Value
import com.example.apptheme.store.SettingsStore
import com.example.core.Services.AppThemeEnum

interface SettingsPage {
    val settings: Value<SettingsStore.SettingsUIState>

    fun changeAppTheme(to: AppThemeEnum)
    fun logOut()
}




