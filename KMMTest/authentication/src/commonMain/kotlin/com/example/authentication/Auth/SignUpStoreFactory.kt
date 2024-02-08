package com.example.authentication.Auth

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.example.core.MVI.BaseExecutor
import com.example.corenetwork.api.Auth.AuthApi
import com.example.corenetwork.api.SecurePersistant.SettingsPersistent
import com.example.corenetwork.model.Auth.SignUpRequestEntity
import kotlinx.coroutines.CancellationException

interface SignUpStore : Store<SignUpStore.Intent, SignUpStore.UISignUpState, Nothing> {
    data class UISignUpState(
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

class SignUpStoreFactory(
    private val storeFactory: StoreFactory,
    private val authService: AuthApi,
    private val settings: SettingsPersistent
) {
    fun create(): SignUpStore = object :
        SignUpStore,
        Store<SignUpStore.Intent, SignUpStore.UISignUpState, Nothing> by storeFactory.create(
            name = SignUpStore::class.simpleName,
            initialState = SignUpStore.UISignUpState(),
            bootstrapper = null,
            executorFactory = {
                SignUpExecutor(authService, settings)
            },
            reducer = SignUpReducer()
        ) {}

    sealed interface SignUpMessage {
        data class NicknameField(val field: SignUpStore.Field): SignUpMessage
        data class PhoneNumberField(val field: SignUpStore.Field): SignUpMessage
        data class PasswordField(val field: SignUpStore.Field): SignUpMessage
        data object Success: SignUpMessage
        data class OnError(val s: String): SignUpMessage
        data object ResetError: SignUpMessage
    }

    internal class SignUpExecutor(
        private val authService: AuthApi,
        private val settings: SettingsPersistent
    ): BaseExecutor<SignUpStore.Intent, Nothing, SignUpStore.UISignUpState, SignUpMessage, Nothing>() {
        override suspend fun suspendExecuteIntent(
            intent: SignUpStore.Intent,
            getState: () -> SignUpStore.UISignUpState
        ) = when (intent) {
            is SignUpStore.Intent.StartFlow -> {}
            is SignUpStore.Intent.ValidateNickname -> validateNickName(intent.text)
            is SignUpStore.Intent.ValidatePhoneNumber -> validatePhoneNumber(intent.text)
            is SignUpStore.Intent.ValidatePassword -> validatePassword(intent.text)
            is SignUpStore.Intent.TryAuthPassword -> passwordAuth(
                getState()
            )
            else -> {}
        }

        private fun validateNickName(t: String) {
            if (t.isEmpty()) {
                dispatch(SignUpMessage.NicknameField(SignUpStore.Field(t, isError = true)))
                return
            }
            dispatch(SignUpMessage.NicknameField(SignUpStore.Field(t, isValid = true)))
        }

        private fun validatePhoneNumber(t: String) {
            if (t.isEmpty()) {
                dispatch(SignUpMessage.PhoneNumberField(SignUpStore.Field(t)))
                return
            }

//            val regex = Regex("^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}\$")
//            if (regex.matches(t))
                dispatch(SignUpMessage.PhoneNumberField(field = SignUpStore.Field(t, isValid = true)))
//            else
//                dispatch(AuthMessage.PhoneNumberField(field = AuthStore.Field(t, isError = true)))
        }

        private fun validatePassword(t: String) {
            if (t.isEmpty()) {
                dispatch(SignUpMessage.PasswordField(field = SignUpStore.Field(t)))
                return
            }

            val regex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}\$")
            if (regex.matches(t))
                dispatch(SignUpMessage.PasswordField(field = SignUpStore.Field(t, isValid = true)))
            else
                dispatch(SignUpMessage.PasswordField(field = SignUpStore.Field(t, isError = true)))
        }

        private suspend fun tokenAuth() {
            try {
                val state = authService.trySignInWithToken()
                if (state.loginState.isAuthorized) dispatch(SignUpMessage.Success) else dispatch(
                    SignUpMessage.OnError("Empty")
                )
            } catch(e: Exception) {
                dispatch(SignUpMessage.OnError(e.message ?: ""))
            }
        }

        private suspend fun passwordAuth(
            currentState: SignUpStore.UISignUpState
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
                            dispatch(SignUpMessage.Success)
                        }
                       else -> throw CancellationException("")
                    }
                } catch (e: Exception) {
                    println("errrrororor: $e")
                    dispatch(SignUpMessage.OnError(e.message ?: ""))
                }
            }
        }
    }

    internal class SignUpReducer: Reducer<SignUpStore.UISignUpState, SignUpMessage> {
        override fun SignUpStore.UISignUpState.reduce(
            msg: SignUpMessage
        ): SignUpStore.UISignUpState = when (msg) {
            is SignUpMessage.NicknameField -> copy(
                nickname = msg.field
            )
            is SignUpMessage.PhoneNumberField -> copy(
                phoneNumberState = msg.field
            )
            is SignUpMessage.PasswordField -> copy(
                passwordState = msg.field
            )
            is SignUpMessage.OnError -> copy(
                errorMessage = msg.s
            )
            is SignUpMessage.Success -> copy(
                isSuccess = true
            )
            is SignUpMessage.ResetError -> copy(
                errorMessage = null
            )
        }
    }
}
