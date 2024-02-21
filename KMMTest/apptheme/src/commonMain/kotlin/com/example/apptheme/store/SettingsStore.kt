package com.example.apptheme.store

import com.arkivanov.mvikotlin.core.store.Store
import com.example.core.Services.AppThemeEnum

interface SettingsStore : Store<SettingsStore.Intent, SettingsStore.SettingsUIState, Nothing> {

    sealed interface Intent {
        data class ChangeAppTheme(val new: AppThemeEnum): Intent
    }

    data class SettingsUIState(
        val userName: String = "",
        val userEmail: String = "",
        val appThemeSection: AppThemeSection = AppThemeSection()
    )

    data class AppThemeSection(
        val selectedTheme: AppThemeEnum = AppThemeEnum.DEFAULT
    )

    sealed interface Action {
        data object LoadUserInfo: Action
        data object CheckCurrentAppTheme: Action
    }
}