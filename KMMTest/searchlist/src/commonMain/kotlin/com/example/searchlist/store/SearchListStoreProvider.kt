package com.example.searchlist.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.example.core.MVI.BaseExecutor
import com.example.corenetwork.api.Auth.UserBaseInfo
import com.example.corenetwork.api.Users.UsersApi

internal object SearchListStoreProvider {
    fun create(storeFactory: StoreFactory, usersApi: UsersApi): SearchListStore =
        object : SearchListStore, Store<SearchListStore.Intent, SearchListStore.SearchListUIState, Nothing> by storeFactory.create(
            name = SearchListStore::class.simpleName,
            initialState = SearchListStore.SearchListUIState(),
            bootstrapper = SimpleBootstrapper(SearchListStore.Action.LoadUsersInitially),
            executorFactory = {
                ExecutorImpl(usersApi)
            },
            reducer = ReducerImpl
        ) {}

    private sealed class Message {
        data class RefreshUsersData(val users: List<UserBaseInfo>): Message()
    }

    private class ExecutorImpl(
        private val usersApi: UsersApi
    ): BaseExecutor<SearchListStore.Intent, SearchListStore.Action, SearchListStore.SearchListUIState, Message, Nothing>() {
        override suspend fun suspendExecuteIntent(
            intent: SearchListStore.Intent,
            getState: () -> SearchListStore.SearchListUIState
        ) = when(intent) {
            is SearchListStore.Intent.LoadUsers -> {}
        }

        override suspend fun suspendExecuteAction(
            action: SearchListStore.Action,
            getState: () -> SearchListStore.SearchListUIState
        ) = when(action) {
            is SearchListStore.Action.LoadUsersInitially -> getUsers()
        }

        private suspend fun getUsers() {
            val users = usersApi.getAllUsers(true)
            dispatch(Message.RefreshUsersData(users))
        }
    }

    private object ReducerImpl: Reducer<SearchListStore.SearchListUIState, Message> {
        override fun SearchListStore.SearchListUIState.reduce(
            msg: Message
        ): SearchListStore.SearchListUIState = when(msg) {
            is Message.RefreshUsersData -> copy(
                users = msg.users
            )
        }
    }
}