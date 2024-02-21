package com.example.mykmmtest.utils

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.subscribe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

fun <T: Any> StateFlow<T>.value(lifecycle: Lifecycle): Value<T> {
    val value = MutableValue(value)
    var scope: CoroutineScope? = null

    lifecycle.subscribe(
        onCreate = {
            scope = CoroutineScope(Dispatchers.Main.immediate).also {
                it.launch {
                    collect {
                        value.value = it
                    }
                }
            }
        },
        onStop = {
            scope?.cancel()
            scope = null
        }
    )
    return value
}