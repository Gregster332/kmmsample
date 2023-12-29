package com.example.mykmmtest.Storiess.Auth

import com.arkivanov.mvikotlin.core.binder.Binder
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.example.mykmmtest.Services.AuthService.AuthState
import com.example.mykmmtest.Storiess.Main.Presentation.UIMainState
import com.example.mykmmtest.Utils.Mapper
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.time.Duration

class AuthViewModel(
    private val store: AuthStore
): ViewModel() {

    val state: StateFlow<AuthStore.UIAuthState>
        get() = mutableState

    private val binder: Binder
    private val initialState = AuthStore.UIAuthState()
    private val mutableState = MutableStateFlow(initialState)

    init {
        binder = bind(Dispatchers.Main.immediate) {
            store.states.map { it } bindTo (::acceptState)
        }
        binder.start()

        viewModelScope.launch(Dispatchers.Main) {
            mutableState
                .map(::validateCredentials)
                .collect {
                    store.accept(AuthStore.Intent.IsEnableButton(it))
                }
        }
    }

    fun acceptNickname(text: String) {
        store.accept(AuthStore.Intent.ValidateNickname(text))
    }

    fun acceptPhoneNumber(text: String) {
        store.accept(AuthStore.Intent.ValidatePhoneNumber(text))
    }

    fun acceptPassword(text: String) {
        store.accept(AuthStore.Intent.ValidatePassword(text))
    }

    fun trySingInWithToken() {
        store.accept(AuthStore.Intent.TryAuthToken)
    }

    fun trySignUp() {
        store.accept(AuthStore.Intent.TryAuthPassword)
    }

    override fun onCleared() {
        super.onCleared()
        binder.stop()
        store.dispose()
    }

    private fun acceptState(state: AuthStore.UIAuthState) {
        mutableState.value = state
    }

    private fun validateCredentials(creds: AuthStore.UIAuthState): Boolean {
        return creds.nickname.isValid && creds.phoneNumberState.isValid && creds.passwordState.isValid
    }
}