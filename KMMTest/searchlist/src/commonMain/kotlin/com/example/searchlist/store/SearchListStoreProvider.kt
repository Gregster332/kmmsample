package com.example.searchlist.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.example.core.MVI.BaseExecutor
import com.example.corenetwork.api.Auth.LocalCache
import com.example.corenetwork.api.Auth.UserBaseInfo
import com.example.corenetwork.api.Users.UsersApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

internal object SearchListStoreProvider {
    fun create(storeFactory: StoreFactory, usersApi: UsersApi, db: LocalCache): SearchListStore =
        object : SearchListStore, Store<SearchListStore.Intent, SearchListStore.SearchListUIState, Nothing> by storeFactory.create(
            name = SearchListStore::class.simpleName,
            initialState = SearchListStore.SearchListUIState(),
            bootstrapper = SimpleBootstrapper(SearchListStore.Action.LoadUsers),
            executorFactory = {
                ExecutorImpl(usersApi, db)
            },
            reducer = ReducerImpl
        ) {}

    private sealed class Message {
        data class RefreshUsersData(val users: List<UserBaseInfo>): Message()
    }

    private class ExecutorImpl(
        private val usersApi: UsersApi,
        private val db: LocalCache
    ): BaseExecutor<SearchListStore.Intent, SearchListStore.Action, SearchListStore.SearchListUIState, Message, Nothing>() {

        private var debounceJob: Job? = null

        override suspend fun suspendExecuteIntent(
            intent: SearchListStore.Intent,
            getState: () -> SearchListStore.SearchListUIState
        ) = when(intent) {
            is SearchListStore.Intent.TypingText -> typeText(intent.text)
            is SearchListStore.Intent.CacheNewResult -> cacheNewUsers(intent.result)
        }

        override suspend fun suspendExecuteAction(
            action: SearchListStore.Action,
            getState: () -> SearchListStore.SearchListUIState
        ) = when(action) {
            is SearchListStore.Action.LoadUsers -> getUsersFromBD()
        }

        private suspend fun typeText(text: String) {
            debounceJob?.cancel()
            debounceJob = scope.launch {
                delay(1500L)
                if (debounceJob?.isCancelled == false) {
                    val users = usersApi.searchUsersBy(text)
                    dispatch(Message.RefreshUsersData(users))
                }
            }
        }

        private fun getUsersFromBD() {
            val usersDB = db.getAllUsers()
            dispatch(Message.RefreshUsersData(usersDB))
        }

        private suspend fun cacheNewUsers(user: UserBaseInfo) = withContext(Dispatchers.IO) {
            if (db.getUserBy(user.id) == null) {
                db.saveNewUser(user)
            }
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