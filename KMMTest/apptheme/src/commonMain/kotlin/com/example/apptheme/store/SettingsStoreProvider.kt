package com.example.apptheme.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.example.core.MVI.BaseExecutor
import com.example.core.Services.AppThemeEnum
import com.example.core.Services.SettingsKeys
import com.example.core.Services.SettingsPersistent
import com.example.core.Services.SettingsValue
import com.example.core.Services.value
import com.example.corenetwork.api.auth.LocalCache

internal object SettingsStoreProvider {
    fun create(
        storeFactory: StoreFactory,
        localCache: LocalCache,
        settingsPersistent: SettingsPersistent
    ): SettingsStore =
        object : SettingsStore, Store<SettingsStore.Intent, SettingsStore.SettingsUIState, Nothing> by storeFactory.create(
            name = "SettingsStoreProviderStore",
            initialState = SettingsStore.SettingsUIState(),
            bootstrapper = SimpleBootstrapper(
                SettingsStore.Action.LoadUserInfo,
                SettingsStore.Action.CheckCurrentAppTheme
            ),
            executorFactory = {
                ExecutorImpl(localCache, settingsPersistent)
            },
            reducer = ReducerImpl
        ) {}

    private sealed interface Msg {
        data class UserInfoLoaded(val username: String, val email: String): Msg
        data class AppThemeUpdated(val toNew: AppThemeEnum): Msg
    }

    private class ExecutorImpl(
        private val localCache: LocalCache,
        private val settingsPersistent: SettingsPersistent
    ) : BaseExecutor<SettingsStore.Intent, SettingsStore.Action, SettingsStore.SettingsUIState, Msg, Nothing>() {
        override suspend fun suspendExecuteAction(
            action: SettingsStore.Action
        ) = when(action) {
            is SettingsStore.Action.LoadUserInfo -> loadUserInfo()
            is SettingsStore.Action.CheckCurrentAppTheme -> checkAppTheme()
        }


        override suspend fun suspendExecuteIntent(
            intent: SettingsStore.Intent
        ) = when (intent) {
            is SettingsStore.Intent.ChangeAppTheme -> changeAppTheme(intent.new)
        }

        private fun checkAppTheme() {
            (settingsPersistent.get(
                SettingsValue.StringValue(SettingsKeys.currentAppTheme)
            ).value() as? String)?.let {
                dispatch(Msg.AppThemeUpdated(AppThemeEnum.valueOf(it)))
            }
        }

        private fun loadUserInfo() {
            localCache.getCurrentUser()?.let {
                dispatch(Msg.UserInfoLoaded(it.nickname, it.email))
            }
        }

        private fun changeAppTheme(new: AppThemeEnum) {
            settingsPersistent.add(
                SettingsValue.StringValue(SettingsKeys.currentAppTheme, new.name)
            )
            dispatch(Msg.AppThemeUpdated(new))
        }
    }

    private object ReducerImpl : Reducer<SettingsStore.SettingsUIState, Msg> {
        override fun SettingsStore.SettingsUIState.reduce(message: Msg): SettingsStore.SettingsUIState =
            when (message) {
                is Msg.UserInfoLoaded -> copy(userName = message.username, userEmail = message.email)
                is Msg.AppThemeUpdated -> copy(appThemeSection = appThemeSection.copy(message.toNew))
            }
    }
}
