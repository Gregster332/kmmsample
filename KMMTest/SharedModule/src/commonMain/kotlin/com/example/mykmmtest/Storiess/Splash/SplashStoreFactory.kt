package com.example.mykmmtest.Storiess.Splash

import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.store.create
import com.example.mykmmtest.Services.AuthService.AuthService
import com.example.mykmmtest.Storiess.Main.BaseExecutor
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

interface SplashStore: Store<SplashStore.Intent, SplashStore.UISplashState, Nothing> {
    data class UISplashState(
        val isLoading: Boolean = false,
        val authState: AuthorizeState = AuthorizeState.NotSet
    )

    sealed interface AuthorizeState {
        data object NotSet: AuthorizeState
        data object Autheticated: AuthorizeState
        data object Reauth: AuthorizeState
    }

    sealed interface Intent {
        data object StartFlow: Intent
    }
}

class SplashStoreFactory(
    private val storeFactory: StoreFactory,
    private val authService: AuthService
) {

    sealed interface SplashMessage {
        data object Loading: SplashMessage
        data object AuthAlready: SplashMessage
        data object NeedsReauth: SplashMessage
    }

    fun create(): SplashStore = object :
        SplashStore,
        Store<SplashStore.Intent, SplashStore.UISplashState, Nothing> by storeFactory.create(
            name = SplashStore::class.simpleName,
            initialState = SplashStore.UISplashState(),
            executorFactory = {
                SplashExecutor(authService)
            },
            reducer = SplashReducer()
        ) {}
}

internal class SplashExecutor(
    private val authService: AuthService
): BaseExecutor<SplashStore.Intent, Nothing, SplashStore.UISplashState, SplashStoreFactory.SplashMessage, Nothing>() {
    override suspend fun suspendExecuteIntent(
        intent: SplashStore.Intent,
        getState: () -> SplashStore.UISplashState
    ) = when(intent) {
        is SplashStore.Intent.StartFlow -> checkAuthStatus()
    }

    private suspend fun checkAuthStatus() {
        dispatch(SplashStoreFactory.SplashMessage.Loading)
        try {
            delay(4000L)
            val status = authService.trySignInWithToken()
            if (status.state.isAuthorized)
                dispatch(SplashStoreFactory.SplashMessage.AuthAlready)
            else
                dispatch(SplashStoreFactory.SplashMessage.NeedsReauth)
        } catch(e: Exception) {
            println(e)
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
    }
}