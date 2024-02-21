package com.example.searchlist.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.example.core.MVI.BaseExecutor
import com.example.corenetwork.api.auth.DBUser
import com.example.corenetwork.api.auth.LocalCache
import com.example.corenetwork.api.auth.mapToRequestModel
import com.example.corenetwork.api.chats.ChatsApi
import com.example.corenetwork.api.chats.CreateFaceToFaceChatRequest
import com.example.corenetwork.api.users.UsersApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal object SearchListStoreProvider {
    fun create(
        storeFactory: StoreFactory,
        usersApi: UsersApi,
        chatsApi: ChatsApi,
        db: LocalCache,
        newChatCreatedCallback: () -> Unit,
    ): SearchListStore = object :
            SearchListStore,
            Store<SearchListStore.Intent, SearchListStore.SearchListUIState, Nothing> by storeFactory.create(
                name = SearchListStore::class.simpleName,
                initialState = SearchListStore.SearchListUIState(),
                bootstrapper = SimpleBootstrapper(SearchListStore.Action.LoadUsers),
                executorFactory = {
                    ExecutorImpl(usersApi, chatsApi, db, newChatCreatedCallback)
                },
                reducer = ReducerImpl,
            ) {}

    private sealed class Message {
        data class RefreshUsersData(val users: List<DBUser>) : Message()
    }

    private class ExecutorImpl(
        private val usersApi: UsersApi,
        private val chatsApi: ChatsApi,
        private val db: LocalCache,
        private val newChatCreatedCallback: () -> Unit,
    ) : BaseExecutor<SearchListStore.Intent, SearchListStore.Action, SearchListStore.SearchListUIState, Message, Nothing>() {
        private var debounceJob: Job? = null

        override suspend fun suspendExecuteIntent(
            intent: SearchListStore.Intent
        ) = when (intent) {
            is SearchListStore.Intent.TypingText -> typeText(intent.text)
            is SearchListStore.Intent.CacheNewResult -> cacheNewUsers(intent.result)
        }

        override suspend fun suspendExecuteAction(
            action: SearchListStore.Action
        ) = when (action) {
            is SearchListStore.Action.LoadUsers -> getUsersFromBD()
        }

        private suspend fun typeText(text: String) {
            debounceJob?.cancel()
            debounceJob =
                scope.launch {
                    delay(1500L)
                    if (debounceJob?.isCancelled == false) {
                        val users = usersApi.searchUsersBy(text).map { it.mapToRequestModel(false) }
                        dispatch(Message.RefreshUsersData(users))
                    }
                }
        }

        private fun getUsersFromBD() {
            val usersDB = db.getAllUsers()
            dispatch(Message.RefreshUsersData(usersDB))
        }

        private suspend fun cacheNewUsers(user: DBUser): Unit = withContext(Dispatchers.IO) {
                if (db.getUserBy(user.id) == null) {
                    db.saveNewUser(user)
                }

                when (val current = db.getCurrentUser()) {
                    is DBUser -> {
                        val entity =
                            CreateFaceToFaceChatRequest(
                                user.nickname,
                                current.id,
                                user.id,
                            )
                        chatsApi.createNewChat(entity)?.let {
                            withContext(Dispatchers.Main) {
                                newChatCreatedCallback()
                            }
                        }
                    }
                    else -> {}
                }
            }
    }

    private object ReducerImpl : Reducer<SearchListStore.SearchListUIState, Message> {
        override fun SearchListStore.SearchListUIState.reduce(msg: Message): SearchListStore.SearchListUIState =
            when (msg) {
                is Message.RefreshUsersData ->
                    copy(
                        users = msg.users,
                    )
            }
    }
}
