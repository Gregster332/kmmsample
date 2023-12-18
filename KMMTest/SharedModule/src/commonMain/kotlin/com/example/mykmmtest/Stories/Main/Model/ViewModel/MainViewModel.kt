package com.example.mykmmtest.Stories.Main.Model.ViewModel

import com.arkivanov.mvikotlin.core.binder.Binder
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.example.mykmmtest.Stories.Main.Model.MainStore
import com.example.mykmmtest.Stories.Main.Model.Presentation.UIMainState
import com.example.mykmmtest.Utils.AnyFlow
import com.example.mykmmtest.Utils.Mapper
import com.example.mykmmtest.Utils.wrapToAny
import dev.icerock.moko.mvvm.flow.CStateFlow
import dev.icerock.moko.mvvm.flow.cMutableStateFlow
import dev.icerock.moko.mvvm.flow.cStateFlow
import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

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
    }

    private fun acceptState(state: UIMainState) {
        mutableState.value = state
    }

    override fun onCleared() {
        super.onCleared()
        binder.stop()
        store.dispose()
    }
}