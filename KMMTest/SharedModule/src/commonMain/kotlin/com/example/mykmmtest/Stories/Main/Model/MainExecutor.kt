package com.example.mykmmtest.Stories.Main.Model

import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.mykmmtest.Models.Post
import com.example.mykmmtest.Services.PostLoader
import com.example.mykmmtest.Services.Result
import com.example.mykmmtest.Stories.Main.Model.Factory.MainStoreFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

internal abstract class BaseExecutor<in Intent : Any, in Action : Any, in State : Any, Message : Any, Label : Any>(
    mainContext: CoroutineContext = Dispatchers.Main,
) : CoroutineExecutor<Intent, Action, State, Message, Label>(mainContext = mainContext) {

    final override fun executeIntent(intent: Intent, getState: () -> State) {
        scope.launch {
            suspendExecuteIntent(intent, getState)
        }
    }

    final override fun executeAction(action: Action, getState: () -> State) {
        scope.launch {
            suspendExecuteAction(action, getState)
        }
    }

    open suspend fun suspendExecuteIntent(intent: Intent, getState: () -> State) {}

    open suspend fun suspendExecuteAction(action: Action, getState: () -> State) {}
}

internal class MainExecutor(
    private val repository: PostLoader,
) : BaseExecutor<MainStore.Intent, Nothing, MainStore.State, MainStoreFactory.Message, Nothing>() {

    override suspend fun suspendExecuteIntent(
        intent: MainStore.Intent,
        getState: () -> MainStore.State,
    ) = when (intent) {
        is MainStore.Intent.Load -> loadUserInfo()
    }

    private suspend fun loadUserInfo() {
        dispatch(MainStoreFactory.Message.SetLoading)

        when (val response = repository.fetchAllPosts()) {
            is Result.Success -> dispatch(MainStoreFactory.Message.SetUserInfo(response.data))
            is Result.Failure -> dispatch(MainStoreFactory.Message.SetError)
        }
    }
}