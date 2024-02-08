package com.example.chats.api

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.children.ChildNavState
import com.arkivanov.decompose.router.children.NavState
import com.arkivanov.decompose.router.children.SimpleChildNavState
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.essenty.lifecycle.subscribe
import com.arkivanov.essenty.parcelable.Parcelize
import com.arkivanov.mvikotlin.core.binder.Binder
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.example.chats.api.store.ChatsStore
import com.example.chats.api.store.ChatsStoreProvider
import com.example.core.koin.ComponentKoinContext
import com.example.corenetwork.api.Chats.ChatsApi
import com.example.corenetwork.api.Chats.WebSocketService
import com.example.corenetwork.api.Users.UsersApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import org.koin.core.module.Module
import org.koin.dsl.module

class ChatsComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    chatsApi: ChatsApi
): Chats, ComponentContext by componentContext {
    private val store = instanceKeeper.getStore {
        ChatsStoreProvider(
            storeFactory = storeFactory,
            chatsApi = chatsApi
        ).provide()
    }

    private lateinit var binder: Binder

    override val chats: Value<ChatsStore.ChatsUiState>
        get() = _mutableState

    private val _mutableState = MutableValue(ChatsStore.ChatsUiState())

    init {
        lifecycle.subscribe(
            onCreate = {
                binder = bind(Dispatchers.Main.immediate) {
                    store.states.map { it } bindTo (::acceptState)
                }
                binder.start()
                tryLoadChats()
            },
            onDestroy = {
                binder.stop()
                store.dispose()
            }
        )
    }

    override fun tryLoadChats() {
        println("dsdsdsdsdsds")
        store.accept(ChatsStore.ChatsIntent.TryLoadChats)
    }

    private fun acceptState(state: ChatsStore.ChatsUiState) {
        _mutableState.value = state
    }
}