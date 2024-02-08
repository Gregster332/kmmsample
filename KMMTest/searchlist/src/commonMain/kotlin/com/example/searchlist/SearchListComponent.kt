package com.example.searchlist

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.essenty.lifecycle.subscribe
import  com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.arkivanov.mvikotlin.core.binder.Binder
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.example.corenetwork.api.Auth.DBUser
import com.example.corenetwork.api.Auth.LocalCache
import com.example.corenetwork.api.Chats.ChatsApi
import com.example.corenetwork.api.Users.UsersApi
import com.example.searchlist.store.SearchListStore
import com.example.searchlist.store.SearchListStoreProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*


private fun LifecycleOwner.coroutinesScope(): CoroutineScope {
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    lifecycle.doOnDestroy(scope::cancel)
    return scope
}

class SearchListComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    usersApi: UsersApi,
    chatsApi: ChatsApi,
    db: LocalCache,
    private val onTapUser: () -> Unit
): SearchList, ComponentContext by componentContext, CoroutineScope by componentContext.coroutinesScope() {
    private val store = instanceKeeper.getStore {
        SearchListStoreProvider.create(
            storeFactory = storeFactory,
            usersApi = usersApi,
            chatsApi = chatsApi,
            db = db,
            newChatCreatedCallback = {
                onTapUser()
            }
        )
    }

    override val value: Value<SearchListStore.SearchListUIState>
        get() = _value

    private val _value = MutableValue(SearchListStore.SearchListUIState())
    private lateinit var binder: Binder

    init {
        lifecycle.subscribe(
            onCreate = {
                binder = bind(Dispatchers.Main.immediate) {
                    store.states.map { it } bindTo (::acceptState)
                }
                binder.start()
            },
            onDestroy = {
                binder.stop()
                //store.dispose()
            }
        )
    }

    override fun type(text: String) {
        store.accept(SearchListStore.Intent.TypingText(text))
    }

    override fun cacheResult(user: DBUser) {
        store.accept(SearchListStore.Intent.CacheNewResult(user))
    }

    private fun acceptState(state: SearchListStore.SearchListUIState) {
        _value.value = state
    }
}
