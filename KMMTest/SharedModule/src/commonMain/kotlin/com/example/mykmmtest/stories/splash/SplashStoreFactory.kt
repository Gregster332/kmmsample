package com.example.mykmmtest.stories.splash

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.example.core.MVI.BaseExecutor
import com.example.corenetwork.api.auth.AuthApi
import com.example.corenetwork.api.auth.LocalCache
import com.example.corenetwork.api.devicesensorsapi.DeviceSensorApi

interface SplashStore : Store<SplashStore.Intent, SplashStore.UISplashState, Nothing> {
    data class UISplashState(
        val isLoading: Boolean = false,
        val authState: AuthorizeState = AuthorizeState.NotSet,
    )

    sealed interface AuthorizeState {
        data object NotSet : AuthorizeState
        data object Autheticated : AuthorizeState
        data object NeedRefreshToken : AuthorizeState
        data object Reauth : AuthorizeState
    }

    sealed interface Intent {
        data object RefreshToken : Intent
    }

    sealed interface Action {
        data object TryAuthWithToken : Action
        data object StartListenShake: Action
    }
}

class SplashStoreFactory(
    private val storeFactory: StoreFactory,
    private val authService: AuthApi,
    private val localCache: LocalCache,
    private val deviceSensorApi: DeviceSensorApi,
    private val didSensorSignal: () -> Unit
) {
    sealed interface SplashMessage {
        data object Loading : SplashMessage
        data object AuthAlready : SplashMessage
        data object NeedsReauth : SplashMessage
        data object NeedsRefreshToken : SplashMessage
    }

    fun create(): SplashStore = object :
        SplashStore,
        Store<SplashStore.Intent, SplashStore.UISplashState, Nothing> by storeFactory.create(
            name = SplashStore::class.simpleName,
            initialState = SplashStore.UISplashState(),
            bootstrapper = SimpleBootstrapper(
                SplashStore.Action.StartListenShake,
                SplashStore.Action.TryAuthWithToken
            ),
            executorFactory = {
                SplashExecutor(authService, localCache, deviceSensorApi, didSensorSignal)
            },
            reducer = SplashReducer(),
        ) {}
}

internal class SplashExecutor(
    private val authService: AuthApi,
    private val localCache: LocalCache,
    private val deviceSensorApi: DeviceSensorApi,
    private val didSensorSignal: () -> Unit
) : BaseExecutor<SplashStore.Intent, SplashStore.Action, SplashStore.UISplashState, SplashStoreFactory.SplashMessage, Nothing>() {
    override suspend fun suspendExecuteIntent(
        intent: SplashStore.Intent
    ) = when (intent) {
            is SplashStore.Intent.RefreshToken -> refreshIdToken()
        }

    override suspend fun suspendExecuteAction(action: SplashStore.Action) =
        when (action) {
            is SplashStore.Action.TryAuthWithToken -> checkAuthStatus()
            is SplashStore.Action.StartListenShake -> listenDeviceShake()
        }

    private fun listenDeviceShake() {
        deviceSensorApi.start()
        deviceSensorApi.setSensorListener {
            didSensorSignal()
        }
    }

    private suspend fun checkAuthStatus() {
        dispatch(SplashStoreFactory.SplashMessage.Loading)

        try {
            val status = authService.trySignInWithToken()

            if (status.loginState.isAuthorized) {
                dispatch(SplashStoreFactory.SplashMessage.AuthAlready)
            } else if (status.loginState.isAccessTokenExpired) {
                dispatch(SplashStoreFactory.SplashMessage.NeedsRefreshToken)
            } else {
                dispatch(SplashStoreFactory.SplashMessage.NeedsReauth)
            }
        } catch (e: Exception) {
            println(e)
            dispatch(SplashStoreFactory.SplashMessage.NeedsReauth)
        }
    }

    private suspend fun refreshIdToken() {
        try {
            authService.refreshIdToken()
            dispatch(SplashStoreFactory.SplashMessage.AuthAlready)
        } catch (e: Exception) {
            dispatch(SplashStoreFactory.SplashMessage.NeedsReauth)
        }
    }
}

internal class SplashReducer : Reducer<SplashStore.UISplashState, SplashStoreFactory.SplashMessage> {
    override fun SplashStore.UISplashState.reduce(msg: SplashStoreFactory.SplashMessage) = when (msg) {
        is SplashStoreFactory.SplashMessage.Loading ->
            copy(
                isLoading = true,
            )
        is SplashStoreFactory.SplashMessage.AuthAlready ->
            copy(
                isLoading = false,
                authState = SplashStore.AuthorizeState.Autheticated,
            )
        is SplashStoreFactory.SplashMessage.NeedsReauth ->
            copy(
                isLoading = false,
                authState = SplashStore.AuthorizeState.Reauth,
            )
        is SplashStoreFactory.SplashMessage.NeedsRefreshToken ->
            copy(
                isLoading = false,
                authState = SplashStore.AuthorizeState.NeedRefreshToken,
            )
    }
}
