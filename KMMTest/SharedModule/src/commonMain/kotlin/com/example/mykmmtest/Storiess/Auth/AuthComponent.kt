package com.example.mykmmtest.Storiess.Auth

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.ObserveLifecycleMode
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.subscribe
import com.arkivanov.mvikotlin.core.binder.Binder
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.example.corenetwork.api.Auth.AuthApi
import com.example.corenetwork.api.SecurePersistant.SettingsPersistent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface Auth {
    val state: Value<AuthStore.UIAuthState>

    fun authWithPassword()
    fun validateNickname(text: String)
    fun validatePhone(text: String)
    fun validatePassword(text: String)
    fun openAppTheme()
}

class AuthComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    authApi: AuthApi,
    settings: SettingsPersistent,
    private val onAppTheme: () -> Unit,
    private val onSuccess: () -> Unit
): Auth, ComponentContext by componentContext, KoinComponent {
//    private val koinContext = instanceKeeper.getOrCreate {
//        ComponentKoinContext()
//    }
//
//    private val scope = koinContext.getOrCreateKoinScope(
//        listOf(services(), storeFactoryModule)
//    )

    private lateinit var binder: Binder

    private val store = instanceKeeper.getStore {
        AuthStoreFactory(
            storeFactory = storeFactory,
            authService = authApi,
            settings = settings
        ).create()
    }

    override val state: Value<AuthStore.UIAuthState>
        get() = _state

    private val _state = MutableValue(AuthStore.UIAuthState())

    init {
        lifecycle.subscribe(
            onCreate = {
                println("AuthComponent on create")
                binder = bind(Dispatchers.Main.immediate) {
                    store.states.map { it } bindTo {
                        _state.value = it
                        if (it.isSuccess) {
                            onSuccess()
                        }
                    }
                }
                binder.start()
                store.accept(AuthStore.Intent.StartFlow)
                //webSocketService.connect()
            },
            onStop = {
                println("AuthComponent on stop")
                //webSocketService.disconnect()
                binder.stop()
            },
            onDestroy = {
                //k.cancel()
                println("AuthComponent on destroy")
            }
        )

//        webSocketService.messageListenerBlock = {
//            println(it)
//        }
//        scope {
//            store.states
//                .map { it.nickname.isValid && it.phoneNumberState.isValid && it.passwordState.isValid }
//                .collect {
//                    _state.value.copy(isButtonEnabled = it)
//                }
//        }
    }

    override fun authWithPassword() {
        store.accept(AuthStore.Intent.TryAuthPassword)
    }

    override fun validateNickname(text: String) {
        store.accept(AuthStore.Intent.ValidateNickname(text))
    }

    override fun validatePhone(text: String) {
        store.accept(AuthStore.Intent.ValidatePhoneNumber(text))
    }

    override fun validatePassword(text: String) {
        store.accept(AuthStore.Intent.ValidatePassword(text))
    }

    override fun openAppTheme() {
        onAppTheme()
    }
}
