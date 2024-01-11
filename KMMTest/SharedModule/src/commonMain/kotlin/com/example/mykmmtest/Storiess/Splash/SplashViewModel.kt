package com.example.mykmmtest.Storiess.Splash

import com.arkivanov.mvikotlin.core.binder.Binder
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.states
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

class SplashViewModel(
    private val store: SplashStore
): ViewModel() {

    val state: StateFlow<SplashStore.UISplashState>
        get() = mutableState

    private val binder: Binder
    private val initialState = SplashStore.UISplashState()
    private val mutableState = MutableStateFlow(initialState)

    init {
        binder = bind(Dispatchers.Main.immediate) {
            store.states.map { it } bindTo (::acceptState)
        }
        binder.start()
        store.accept(SplashStore.Intent.StartFlow)
    }

    private suspend fun acceptState(state: SplashStore.UISplashState) {
        mutableState.emit(state)
    }

    override fun onCleared() {
        super.onCleared()
        binder.stop()
        store.dispose()
    }
}