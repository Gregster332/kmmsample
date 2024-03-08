package com.example.authentication.Auth

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.example.core.MVI.BaseExecutor
import com.example.core.Services.SettingsPersistent
import com.example.corenetwork.api.auth.AuthApi
import com.example.corenetwork.api.auth.LocalCache
import com.example.corenetwork.api.auth.UserBaseInfo
import com.example.corenetwork.api.auth.mapToRequestModel
import com.example.corenetwork.api.users.UsersApi
import com.example.corenetwork.model.auth.SignUpRequestEntity
import kotlinx.coroutines.async

interface SignUpStore : Store<SignUpStore.Intent, SignUpStore.UISignUpState, Nothing> {
    data class UISignUpState(
        val nickname: Field = Field(),
        val phoneNumberState: Field = Field(),
        val passwordState: Field = Field(),
        val errorMessage: String? = null,
        val isSuccess: Boolean = false,
    ) : Intent

    data class Field(
        var text: String = "",
        var isError: Boolean = false,
        var isValid: Boolean = false,
    )

    sealed interface Intent {
        data object StartFlow : Intent
        data class ValidateNickname(val text: String) : Intent
        data class ValidatePhoneNumber(val text: String) : Intent
        data class ValidatePassword(val text: String) : Intent
        data class TryAuthPassword(val entity: SignUpRequestEntity) : Intent
    }
}

object SignUpStoreFactory {
    fun create(
        storeFactory: StoreFactory,
        authService: AuthApi,
        usersApi: UsersApi,
        localCache: LocalCache
    ): SignUpStore = object :
            SignUpStore,
            Store<SignUpStore.Intent, SignUpStore.UISignUpState, Nothing> by storeFactory.create(
                name = SignUpStore::class.simpleName,
                initialState = SignUpStore.UISignUpState(),
                bootstrapper = null,
                executorFactory = {
                    SignUpExecutor(authService, usersApi, localCache)
                },
                reducer = SignUpReducer(),
            ) {}

    sealed interface SignUpMessage {
        data class NicknameField(val field: SignUpStore.Field) : SignUpMessage
        data class PhoneNumberField(val field: SignUpStore.Field) : SignUpMessage
        data class PasswordField(val field: SignUpStore.Field) : SignUpMessage
        data object Success : SignUpMessage
        data class OnError(val s: String) : SignUpMessage
        data object ResetError : SignUpMessage
    }

    internal class SignUpExecutor(
        private val authService: AuthApi,
        private val usersApi: UsersApi,
        private val localCache: LocalCache,
    ) : BaseExecutor<SignUpStore.Intent, Nothing, SignUpStore.UISignUpState, SignUpMessage, Nothing>() {
        override suspend fun suspendExecuteIntent(
            intent: SignUpStore.Intent
        ) = when (intent) {
            is SignUpStore.Intent.StartFlow -> {}
            is SignUpStore.Intent.ValidateNickname -> validateNickName(intent.text)
            is SignUpStore.Intent.ValidatePhoneNumber -> validatePhoneNumber(intent.text)
            is SignUpStore.Intent.ValidatePassword -> validatePassword(intent.text)
            is SignUpStore.Intent.TryAuthPassword -> passwordAuth(intent.entity)
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
            if (regex.matches(t)) {
                dispatch(SignUpMessage.PasswordField(field = SignUpStore.Field(t, isValid = true)))
            } else {
                dispatch(SignUpMessage.PasswordField(field = SignUpStore.Field(t, isError = true)))
            }
        }

        private suspend fun tokenAuth() {
            try {
                val state = authService.trySignInWithToken()
                if (state.loginState.isAuthorized) {
                    dispatch(SignUpMessage.Success)
                } else {
                    dispatch(
                        SignUpMessage.OnError("Empty"),
                    )
                }
            } catch (e: Exception) {
                dispatch(SignUpMessage.OnError(e.message ?: ""))
            }
        }

        private suspend fun passwordAuth(entity: SignUpRequestEntity) {
            val currentUser = scope.async {
                try {
                    val tokens = authService.generateToken(entity)
                    val user = usersApi.getUserById(tokens?.userId ?: "")
                    user
                } catch (e: Exception) {
                    null
                }
            }

            currentUser.await()?.let {
                if (localCache.getUserBy(it.id) != null) {
                    dispatch(SignUpMessage.Success)
                    return
                }

                localCache.saveNewUser(it.mapToRequestModel(true))
                dispatch(SignUpMessage.Success)
            } ?: dispatch(SignUpMessage.OnError("error here"))
        }
    }

    internal class SignUpReducer : Reducer<SignUpStore.UISignUpState, SignUpMessage> {
        override fun SignUpStore.UISignUpState.reduce(msg: SignUpMessage): SignUpStore.UISignUpState = when (msg) {
                is SignUpMessage.NicknameField ->
                    copy(
                        nickname = msg.field,
                    )
                is SignUpMessage.PhoneNumberField ->
                    copy(
                        phoneNumberState = msg.field,
                    )
                is SignUpMessage.PasswordField ->
                    copy(
                        passwordState = msg.field,
                    )
                is SignUpMessage.OnError ->
                    copy(
                        errorMessage = msg.s,
                    )
                is SignUpMessage.Success ->
                    copy(
                        isSuccess = true,
                    )
                is SignUpMessage.ResetError ->
                    copy(
                        errorMessage = null,
                    )
            }
    }
}
