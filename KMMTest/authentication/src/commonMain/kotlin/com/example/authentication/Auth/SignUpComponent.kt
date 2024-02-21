package com.example.authentication.Auth

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.subscribe
import com.arkivanov.mvikotlin.core.binder.Binder
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.example.core.Services.SettingsPersistent
import com.example.corenetwork.api.auth.AuthApi
import com.example.corenetwork.model.auth.SignUpRequestEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent

interface SignUp {
    val state: Value<SignUpStore.UISignUpState>

    fun authWithPassword()
    fun validateNickname(text: String)
    fun validatePhone(text: String)
    fun validatePassword(text: String)
}

class SignUpComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    authApi: AuthApi,
    private val settings: SettingsPersistent,
    private val onSuccess: () -> Unit,
) : SignUp, ComponentContext by componentContext, KoinComponent {
    private lateinit var binder: Binder

    private val store =
        instanceKeeper.getStore {
            SignUpStoreFactory(
                storeFactory = storeFactory,
                authService = authApi,
                settings = settings,
            ).create()
        }

    override val state: Value<SignUpStore.UISignUpState>
        get() = _state

    private val _state = MutableValue(SignUpStore.UISignUpState())

    init {
        lifecycle.subscribe(
            onCreate = {
                binder =
                    bind(Dispatchers.Main.immediate) {
                        store.states.map { it } bindTo {
                            _state.value = it
                            if (it.isSuccess) {
                                onSuccess()
                            }
                        }
                    }
                binder.start()
                store.accept(SignUpStore.Intent.StartFlow)
            },
            onStop = {
                binder.stop()
            },
        )
    }

    override fun authWithPassword() {
        val entity = SignUpRequestEntity(
            _state.value.nickname.text,
            _state.value.phoneNumberState.text,
            _state.value.passwordState.text
        )
        store.accept(SignUpStore.Intent.TryAuthPassword(entity))
    }

    override fun validateNickname(text: String) {
        store.accept(SignUpStore.Intent.ValidateNickname(text))
    }

    override fun validatePhone(text: String) {
        store.accept(SignUpStore.Intent.ValidatePhoneNumber(text))
    }

    override fun validatePassword(text: String) {
        store.accept(SignUpStore.Intent.ValidatePassword(text))
    }
}
