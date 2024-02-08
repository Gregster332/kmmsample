package com.example.searchlist

import com.arkivanov.decompose.value.Value
import com.example.corenetwork.api.Auth.DBUser
import com.example.searchlist.store.SearchListStore

interface SearchList {
    val value: Value<SearchListStore.SearchListUIState>

    fun type(text: String)
    fun cacheResult(user: DBUser)
}