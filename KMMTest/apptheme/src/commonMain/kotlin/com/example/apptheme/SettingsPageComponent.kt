package com.example.apptheme

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.essenty.lifecycle.doOnCreate
import com.arkivanov.essenty.lifecycle.doOnStop
import com.arkivanov.mvikotlin.core.binder.Binder
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.example.apptheme.store.SettingsStore
import com.example.apptheme.store.SettingsStoreProvider
import com.example.core.Services.AppThemeEnum
import com.example.core.koin.ComponentKoinContext
import com.example.core.koin.coreModules
import com.example.corenetwork.di.coreNetworkModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class SettingsPageComponent(
    componentContext: ComponentContext
) : SettingsPage, ComponentContext by componentContext, KoinComponent {

    private val koinContext = instanceKeeper.getOrCreate {
        ComponentKoinContext()
    }

    private val scope = koinContext.getOrCreateKoinScope(
        coreModules + listOf(coreNetworkModule)
        )

    private val store = instanceKeeper.getStore {
        SettingsStoreProvider.create(
            get(),
            get(),
            scope.get()
        )
    }

    private val initial = SettingsStore.SettingsUIState()
    private val _settings = MutableValue(initial)
    private lateinit var binder: Binder
    override val settings = _settings

    init {
        lifecycle.doOnCreate {
            binder = bind(Dispatchers.Main.immediate) {
                store.states.map { it } bindTo (::acceptState)
            }
            binder.start()
        }
        lifecycle.doOnStop {
            binder.stop()
            store.dispose()
        }
    }

    override fun changeAppTheme(to: AppThemeEnum) {
        store.accept(SettingsStore.Intent.ChangeAppTheme(to))
    }

    override fun printAppTheme() {
        println(settings.value)
    }

    private fun acceptState(state: SettingsStore.SettingsUIState) {
        _settings.value = state
    }
}
