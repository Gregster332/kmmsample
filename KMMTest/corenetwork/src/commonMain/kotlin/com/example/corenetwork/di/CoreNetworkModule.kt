package com.example.corenetwork.di

import com.example.corenetwork.api.Auth.AuthApi
import com.example.corenetwork.api.Auth.AuthApiImpl
import com.example.corenetwork.api.Chats.ChatsApi
import com.example.corenetwork.api.Chats.ChatsApiImpl
import com.example.corenetwork.api.SecurePersistant.SettingsPersistent
import com.example.corenetwork.api.SecurePersistant.SettingsPersistentImpl
import com.example.corenetwork.api.Users.SyncService
import com.example.corenetwork.api.Users.UsersApi
import com.example.corenetwork.api.Users.UsersApiImpl
import com.example.corenetwork.helpers.HttpClientProvider
import org.koin.dsl.module

val coreNetworkModule = module {
    single { HttpClientProvider.get() }

    single<SettingsPersistent> {
        SettingsPersistentImpl()
    }

//    single<SyncService> {
//        SyncService
//    }

    single<ChatsApi> {
        ChatsApiImpl(get())
    }

    single<AuthApi> {
        AuthApiImpl(get(), get())
    }

    single<UsersApi> {
        UsersApiImpl(get(), get())
    }
}