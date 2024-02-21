package com.example.core.MVI

import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

abstract class BaseExecutor<in Intent : Any, Action : Any, State : Any, Message : Any, Label : Any>(
    mainContext: CoroutineContext = Dispatchers.Main,
) : CoroutineExecutor<Intent, Action, State, Message, Label>(mainContext = mainContext) {
    final override fun executeIntent(intent: Intent) {
        scope.launch(SupervisorJob() + Dispatchers.Main.immediate) {
            suspendExecuteIntent(intent)
        }
    }


    final override fun executeAction(action: Action) {
        scope.launch(SupervisorJob() + Dispatchers.Main.immediate) {
            suspendExecuteAction(action)
        }
    }

    open suspend fun suspendExecuteIntent(intent: Intent) {}
    open suspend fun suspendExecuteAction(action: Action) {}
}
