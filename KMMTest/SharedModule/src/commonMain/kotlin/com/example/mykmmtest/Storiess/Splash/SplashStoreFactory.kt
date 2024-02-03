package com.example.mykmmtest.Storiess.Splash

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.example.core.MVI.BaseExecutor
import com.example.corenetwork.api.Auth.AuthApi
import com.example.corenetwork.api.Auth.LocalCache

interface SplashStore: Store<SplashStore.Intent, SplashStore.UISplashState, Nothing> {
    data class UISplashState(
        val isLoading: Boolean = false,
        val authState: AuthorizeState = AuthorizeState.NotSet
    )

    sealed interface AuthorizeState {
        data object NotSet: AuthorizeState
        data object Autheticated: AuthorizeState
        data object NeedRefreshToken: AuthorizeState
        data object Reauth: AuthorizeState
    }

    sealed interface Intent {
        data object RefreshToken: Intent
    }

    sealed interface Action {
        data object TryAuthWithToken: Action
    }
}

class SplashStoreFactory(
    private val storeFactory: StoreFactory,
    private val authService: AuthApi,
    private val localCache: LocalCache,
    private val onAuthUser: () -> Unit
) {
    sealed interface SplashMessage {
        data object Loading: SplashMessage
        data object AuthAlready: SplashMessage
        data object NeedsReauth: SplashMessage
        data object NeedsRefreshToken: SplashMessage
    }

    fun create(): SplashStore = object :
        SplashStore,
        Store<SplashStore.Intent, SplashStore.UISplashState, Nothing> by storeFactory.create(
            name = SplashStore::class.simpleName,
            initialState = SplashStore.UISplashState(),
            bootstrapper = SimpleBootstrapper(SplashStore.Action.TryAuthWithToken),
            executorFactory = {
                SplashExecutor(authService, localCache, onAuthUser)
            },
            reducer = SplashReducer()
        ) {}
}

internal class SplashExecutor(
    private val authService: AuthApi,
    private val localCache: LocalCache,
    private val onAuthUser: () -> Unit
): BaseExecutor<SplashStore.Intent, SplashStore.Action, SplashStore.UISplashState, SplashStoreFactory.SplashMessage, Nothing>() {
    override suspend fun suspendExecuteIntent(
        intent: SplashStore.Intent,
        getState: () -> SplashStore.UISplashState
    ) = when(intent) {
        is SplashStore.Intent.RefreshToken -> refreshIdToken()
    }

    override suspend fun suspendExecuteAction(
        action: SplashStore.Action,
        getState: () -> SplashStore.UISplashState
    ) = when (action) {
        is SplashStore.Action.TryAuthWithToken -> checkAuthStatus()
    }

    private suspend fun checkAuthStatus() {
        dispatch(SplashStoreFactory.SplashMessage.Loading)

        //localCache.deleteAllUsers()
        println("Hello db")
        println(localCache.getAllUsers())
        println("bye db")

        try {
            val status = authService.trySignInWithToken()

            if (status.loginState.isAuthorized) {
                dispatch(SplashStoreFactory.SplashMessage.AuthAlready)
            } else if (status.loginState.isAccessTokenExpired) {
                dispatch(SplashStoreFactory.SplashMessage.NeedsRefreshToken)
            } else {
                dispatch(SplashStoreFactory.SplashMessage.NeedsReauth)
            }
        } catch(e: Exception) {
            println(e)
            //dispatch(SplashStoreFactory.SplashMessage.NeedsReauth)
            dispatch(SplashStoreFactory.SplashMessage.AuthAlready)
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

internal class SplashReducer: Reducer<SplashStore.UISplashState, SplashStoreFactory.SplashMessage> {
    override fun SplashStore.UISplashState.reduce(
        msg: SplashStoreFactory.SplashMessage
    ) = when(msg) {
        is SplashStoreFactory.SplashMessage.Loading -> copy(
            isLoading = true
        )
        is SplashStoreFactory.SplashMessage.AuthAlready -> copy(
            isLoading = false,
            authState = SplashStore.AuthorizeState.Autheticated
        )
        is SplashStoreFactory.SplashMessage.NeedsReauth -> copy(
            isLoading = false,
            authState = SplashStore.AuthorizeState.Reauth
        )
        is SplashStoreFactory.SplashMessage.NeedsRefreshToken -> copy(
            isLoading = false,
            authState = SplashStore.AuthorizeState.NeedRefreshToken
        )
    }
}