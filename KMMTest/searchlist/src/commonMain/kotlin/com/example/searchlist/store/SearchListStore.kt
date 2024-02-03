package com.example.searchlist.store

import com.arkivanov.mvikotlin.core.store.Store
import com.example.corenetwork.api.Auth.UserBaseInfo

interface SearchListStore: Store<SearchListStore.Intent, SearchListStore.SearchListUIState, Nothing> {
    sealed interface Intent {
        data class TypingText(val text: String): Intent
        data class CacheNewResult(val result: UserBaseInfo): Intent
    }

    sealed interface Action {
        data object LoadUsers: Action
    }

    data class SearchListUIState(
        val users: List<UserBaseInfo> = emptyList()
    )
}