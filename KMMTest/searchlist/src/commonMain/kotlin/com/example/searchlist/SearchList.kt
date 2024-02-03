package com.example.searchlist

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.example.corenetwork.api.Auth.UserBaseInfo
import com.example.corenetwork.api.Users.UsersApi
import com.example.searchlist.store.SearchListStore

interface SearchList {
    val value: Value<SearchListStore.SearchListUIState>

    fun type(text: String)
    fun cacheResult(user: UserBaseInfo)
}