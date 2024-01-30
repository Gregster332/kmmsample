package com.example.searchlist.store

import com.arkivanov.mvikotlin.core.store.Store
import com.example.corenetwork.api.Auth.UserBaseInfo

interface SearchListStore: Store<SearchListStore.Intent, SearchListStore.SearchListUIState, Nothing> {
    sealed interface Intent {
        data object LoadUsers: Intent
    }

    sealed interface Action {
        data object LoadUsersInitially: Action
    }

    data class SearchListUIState(
        val users: List<UserBaseInfo> = emptyList()
    )
}