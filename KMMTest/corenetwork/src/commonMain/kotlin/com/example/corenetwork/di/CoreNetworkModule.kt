package com.example.corenetwork.di

import com.example.corenetwork.api.auth.AuthApi
import com.example.corenetwork.api.auth.AuthApiImpl
import com.example.corenetwork.api.chats.ChatsApi
import com.example.corenetwork.api.chats.ChatsApiImpl
import com.example.corenetwork.api.*
import com.example.corenetwork.api.users.UsersApi
import com.example.corenetwork.api.users.UsersApiImpl
//import com.example.corenetwork.api.users.UsersApiImpl
import com.example.corenetwork.api.websocket.WebSocketNew
import com.example.corenetwork.api.websocket.WebSocketNewImpl
import com.example.corenetwork.helpers.HttpClientProvider
import org.koin.dsl.module

val coreNetworkModule = module {
        single { HttpClientProvider.get() }

        single<ChatsApi> {
            ChatsApiImpl(get(), get(), get())
        }

        single<WebSocketNew> {
            WebSocketNewImpl()
        }

        single<AuthApi> {
            AuthApiImpl(get(), get(), get())
        }

        single<UsersApi> {
            UsersApiImpl(get(), get())
        }
    }
