package com.example.mykmmtest.Storiess.Main.ViewModel

import com.arkivanov.mvikotlin.core.binder.Binder
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.example.mykmmtest.Services.WsMessage
import com.example.mykmmtest.Storiess.Main.MainStore
import com.example.mykmmtest.Storiess.Main.Presentation.UIMainState
import com.example.mykmmtest.Utils.Mapper
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class MainViewModel(
    private val store: MainStore,
    private val stateMapper: Mapper<MainStore.State, UIMainState>
): ViewModel() {

    val state: StateFlow<UIMainState>
        get () = mutableState

    private val initialState = UIMainState()
    private val mutableState = MutableStateFlow(initialState)
    private val binder: Binder

    init {
        binder = bind(Dispatchers.Main.immediate) {
            store.states.map { value -> stateMapper.map(value) } bindTo (::acceptState)
        }
        binder.start()
        store.accept(MainStore.Intent.Load)
        store.accept(MainStore.Intent.OnConnect)

        viewModelScope.launch {
            mutableState
                .map { it.isLoading }
                .collect {
                    println(it)
                }
        }
    }

    fun tapSendMessage(message: String) {
        val serialized = serializeMessage(message)
        //println(serialized)
        store.accept(MainStore.Intent.OnTapSendMessage(serialized))
    }

    private fun acceptState(state: UIMainState) {
        mutableState.value = state
    }

    private fun serializeMessage(message: String): String {
        var wsmessage = WsMessage("0", "Greg", message)
        return Json.encodeToString(wsmessage)
    }

    override fun onCleared() {
        super.onCleared()
        binder.stop()
        store.dispose()
    }
}