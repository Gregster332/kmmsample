package com.example.apptheme.store

import com.arkivanov.mvikotlin.core.store.Store
import com.example.core.Services.AppThemeEnum

interface SettingsStore : Store<SettingsStore.Intent, SettingsStore.SettingsUIState, Nothing> {

    sealed interface Intent {
        data class ChangeAppTheme(val new: AppThemeEnum): Intent
        data object LogOut: Intent
    }

    data class SettingsUIState(
        val userName: String = "",
        val userEmail: String = "",
        val sections: List<SettingsSection> = listOf(
            SettingsSection.Appearence()
        )
    )

    sealed interface SettingsSection {
        data class Appearence(var selectedTheme: AppThemeEnum = AppThemeEnum.DEFAULT): SettingsSection
        data class Default(val cells: List<SettingsCell>): SettingsSection
    }

    data class SettingsCell(
        val title: String,
        val info: String? = null
    )

    sealed interface Action {
        data object LoadUserInfo: Action
        data object CheckCurrentAppTheme: Action
    }
}