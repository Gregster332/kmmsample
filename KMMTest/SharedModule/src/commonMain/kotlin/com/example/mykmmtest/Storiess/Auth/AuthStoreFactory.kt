package com.example.mykmmtest.Storiess.Auth

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.store.create
import com.example.mykmmtest.Services.AuthService.AuthService
import com.example.mykmmtest.Services.AuthService.AuthState
import com.example.mykmmtest.Services.AuthService.Models.SendAuthRequestEntity
import com.example.mykmmtest.Services.SecurePersistant.SecurePersistant
import com.example.mykmmtest.Storiess.Main.BaseExecutor
import com.example.mykmmtest.Storiess.Main.Factory.MainStoreFactory
import com.example.mykmmtest.Storiess.Main.MainStore
import com.example.mykmmtest.Storiess.Main.Presentation.UIMainState
import kotlinx.coroutines.delay
import kotlin.random.Random

interface AuthStore : Store<AuthStore.Intent, AuthStore.UIAuthState, Nothing> {
    data class UIAuthState internal constructor(
        val nickname: Field = Field(),
        val phoneNumberState: Field = Field(),
        val passwordState: Field = Field(),
        val isButtonEnabled: Boolean = false,
        val errorMessage: String? = null,
        val isSuccess: Boolean = false
    ) : Intent

    data class Field(
        var text: String = "",
        var isError: Boolean = false,
        var isValid: Boolean = false
    )

    sealed interface Intent {
        data class ValidateNickname(val text: String): Intent
        data class ValidatePhoneNumber(val text: String): Intent
        data class ValidatePassword(val text: String): Intent
        data class IsEnableButton(val value: Boolean): Intent
        data object TryAuthToken : Intent
        data object TryAuthPassword : Intent
    }
}

class AuthStoreFactory(
    private val storeFactory: StoreFactory,
    private val authService: AuthService,
) {
    fun create(): AuthStore = object :
        AuthStore,
        Store<AuthStore.Intent, AuthStore.UIAuthState, Nothing> by storeFactory.create(
            name = AuthStore::class.simpleName,
            initialState = AuthStore.UIAuthState(),
            bootstrapper = null,
            executorFactory = {
                AuthExecutor(authService)
            },
            reducer = AuthReducer()
        ) {}

    sealed interface AuthMessage {
        data class NicknameField(val field: AuthStore.Field): AuthMessage
        data class PhoneNumberField(val field: AuthStore.Field): AuthMessage
        data class PasswordField(val field: AuthStore.Field): AuthMessage
        data class IsButtonEnable(val value: Boolean): AuthMessage
        data object Success: AuthMessage
        data class OnError(val s: String): AuthMessage
    }

    internal class AuthExecutor(
        private val authService: AuthService,
    ): BaseExecutor<AuthStore.Intent, Nothing, AuthStore.UIAuthState, AuthMessage, Nothing>() {
        override suspend fun suspendExecuteIntent(
            intent: AuthStore.Intent,
            getState: () -> AuthStore.UIAuthState
        ) = when (intent) {
            is AuthStore.Intent.ValidateNickname -> validateNickName(intent.text)
            is AuthStore.Intent.ValidatePhoneNumber -> validatePhoneNumber(intent.text)
            is AuthStore.Intent.ValidatePassword -> validatePassword(intent.text)
            is AuthStore.Intent.IsEnableButton -> dispatch(AuthMessage.IsButtonEnable(intent.value))
            is AuthStore.Intent.TryAuthToken -> tokenAuth()
            is AuthStore.Intent.TryAuthPassword -> passwordAuth(
                getState()
            )
            else -> {}
        }

        private fun validateNickName(t: String) {
            if (t.isEmpty()) {
                dispatch(AuthMessage.NicknameField(AuthStore.Field(t, isError = true)))
                return
            }
            dispatch(AuthMessage.NicknameField(AuthStore.Field(t, isValid = true)))
        }

        private fun validatePhoneNumber(t: String) {
            if (t.isEmpty()) {
                dispatch(AuthMessage.PhoneNumberField(AuthStore.Field(t)))
                return
            }

            val regex = Regex("^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}\$")
            if (regex.matches(t))
                dispatch(AuthMessage.PhoneNumberField(field = AuthStore.Field(t, isValid = true)))
            else
                dispatch(AuthMessage.PhoneNumberField(field = AuthStore.Field(t, isError = true)))
        }

        private fun validatePassword(t: String) {
            if (t.isEmpty()) {
                dispatch(AuthMessage.PasswordField(field = AuthStore.Field(t)))
                return
            }

            val regex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}\$")
            if (regex.matches(t))
                dispatch(AuthMessage.PasswordField(field = AuthStore.Field(t, isValid = true)))
            else
                dispatch(AuthMessage.PasswordField(field = AuthStore.Field(t, isError = true)))
        }

        private suspend fun tokenAuth() {
            try {
                val state = authService.trySignInWithToken()
                if (state.state.isAuthorized) dispatch(AuthMessage.Success) else dispatch(AuthMessage.OnError("Empty"))
            } catch(e: Exception) {
                dispatch(AuthMessage.OnError(e.message ?: ""))
            }
        }

        private suspend fun passwordAuth(
            currentState: AuthStore.UIAuthState
        ) {
            if (currentState.phoneNumberState.isValid && currentState.passwordState.isValid) {
                try {
                    val entity = SendAuthRequestEntity(
                        currentState.nickname.text,
                        currentState.phoneNumberState.text,
                        currentState.passwordState.text
                    )

                    authService.sendAuthRequest(entity)
                    authService.trySignInWithToken()
                    dispatch(AuthMessage.Success)
                } catch (e: Exception) {
                    println("errrrororor: $e")
                    dispatch(AuthMessage.OnError(e.message ?: ""))
                }
            }
        }
    }

    internal class AuthReducer: Reducer<AuthStore.UIAuthState, AuthMessage> {
        override fun AuthStore.UIAuthState.reduce(
            msg: AuthMessage
        ): AuthStore.UIAuthState = when (msg) {
            is AuthMessage.NicknameField -> copy(
                nickname = msg.field
            )
            is AuthMessage.PhoneNumberField -> copy(
                phoneNumberState = msg.field
            )
            is AuthMessage.PasswordField -> copy(
                passwordState = msg.field
            )
            is AuthMessage.IsButtonEnable -> copy(
                isButtonEnabled = msg.value
            )
            is AuthMessage.OnError -> copy(
                errorMessage = msg.s
            )
            is AuthMessage.Success -> copy(
                isSuccess = true
            )
        }
    }
}

