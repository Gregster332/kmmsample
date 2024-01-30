package com.example.mykmmtest.Storiess.Auth

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.example.core.MVI.BaseExecutor
import com.example.corenetwork.api.Auth.AuthApi
import com.example.corenetwork.api.SecurePersistant.SettingsPersistent
import com.example.corenetwork.model.Auth.SignUpRequestEntity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay

interface AuthStore : Store<AuthStore.Intent, AuthStore.UIAuthState, Nothing> {
    data class UIAuthState(
        val nickname: Field = Field(),
        val phoneNumberState: Field = Field(),
        val passwordState: Field = Field(),
        val errorMessage: String? = null,
        val isSuccess: Boolean = false
    ) : Intent

    data class Field(
        var text: String = "",
        var isError: Boolean = false,
        var isValid: Boolean = false
    )

    sealed interface Intent {
        data object StartFlow: Intent
        data class ValidateNickname(val text: String): Intent
        data class ValidatePhoneNumber(val text: String): Intent
        data class ValidatePassword(val text: String): Intent
        data object TryAuthPassword : Intent
    }
}

class AuthStoreFactory(
    private val storeFactory: StoreFactory,
    private val authService: AuthApi,
    private val settings: SettingsPersistent
) {
    fun create(): AuthStore = object :
        AuthStore,
        Store<AuthStore.Intent, AuthStore.UIAuthState, Nothing> by storeFactory.create(
            name = AuthStore::class.simpleName,
            initialState = AuthStore.UIAuthState(),
            bootstrapper = null,
            executorFactory = {
                AuthExecutor(authService, settings)
            },
            reducer = AuthReducer()
        ) {}

    sealed interface AuthMessage {
        data class NicknameField(val field: AuthStore.Field): AuthMessage
        data class PhoneNumberField(val field: AuthStore.Field): AuthMessage
        data class PasswordField(val field: AuthStore.Field): AuthMessage
        data object Success: AuthMessage
        data class OnError(val s: String): AuthMessage
        data object ResetError: AuthMessage
    }

    internal class AuthExecutor(
        private val authService: AuthApi,
        private val settings: SettingsPersistent
    ): BaseExecutor<AuthStore.Intent, Nothing, AuthStore.UIAuthState, AuthMessage, Nothing>() {
        override suspend fun suspendExecuteIntent(
            intent: AuthStore.Intent,
            getState: () -> AuthStore.UIAuthState
        ) = when (intent) {
            is AuthStore.Intent.StartFlow -> {}
            is AuthStore.Intent.ValidateNickname -> validateNickName(intent.text)
            is AuthStore.Intent.ValidatePhoneNumber -> validatePhoneNumber(intent.text)
            is AuthStore.Intent.ValidatePassword -> validatePassword(intent.text)
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

//            val regex = Regex("^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}\$")
//            if (regex.matches(t))
                dispatch(AuthMessage.PhoneNumberField(field = AuthStore.Field(t, isValid = true)))
//            else
//                dispatch(AuthMessage.PhoneNumberField(field = AuthStore.Field(t, isError = true)))
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
                if (state.isAuthorized) dispatch(AuthMessage.Success) else dispatch(AuthMessage.OnError("Empty"))
            } catch(e: Exception) {
                dispatch(AuthMessage.OnError(e.message ?: ""))
                //dispatch(AuthMessage.ResetError)
            }
        }

        private suspend fun passwordAuth(
            currentState: AuthStore.UIAuthState
        ) {
            if (currentState.phoneNumberState.isValid && currentState.passwordState.isValid) {
                try {
                    val entity = SignUpRequestEntity(
                        nickname = currentState.nickname.text,
                        email = currentState.phoneNumberState.text,
                        password = currentState.passwordState.text
                    )
                    authService.generateToken(entity)
                    when (val token = settings.getString("AUTH_TOKEN")) {
                        is String -> {
                            println("TOKEN: $token")
                            dispatch(AuthMessage.Success)
                        }
                       else -> throw CancellationException("")
                    }
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
            is AuthMessage.OnError -> copy(
                errorMessage = msg.s
            )
            is AuthMessage.Success -> copy(
                isSuccess = true
            )
            is AuthMessage.ResetError -> copy(
                errorMessage = null
            )
        }
    }
}
