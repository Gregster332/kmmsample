package com.example.mykmmtest.DI

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.logging.logger.Logger
import com.arkivanov.mvikotlin.logging.store.LoggingStoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.example.mykmmtest.Services.AuthService.AuthService
import com.example.mykmmtest.Services.AuthService.AuthServiceImpl
import com.example.mykmmtest.Services.PostLoader
import com.example.mykmmtest.Services.WebSocketService
import com.example.mykmmtest.Services.WebSocketServiceImpl
import com.example.mykmmtest.Storiess.Auth.AuthStore
import com.example.mykmmtest.Storiess.Auth.AuthStoreFactory
import com.example.mykmmtest.Storiess.Auth.AuthViewModel
import com.example.mykmmtest.Storiess.Main.Factory.MainStoreFactory
import com.example.mykmmtest.Storiess.Main.MainStore
import com.example.mykmmtest.Storiess.Main.Presentation.MainUIStateMapper
import com.example.mykmmtest.Storiess.Main.Presentation.UIMainState
import com.example.mykmmtest.Storiess.Main.ViewModel.MainViewModel
import com.example.mykmmtest.Utils.Mapper
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(
    appDeclaration: KoinAppDeclaration = {},
) {
    startKoin {
        appDeclaration
        modules(
            mainModule,
            authModule,
            services(),
            settingsModule()
        )
    }
}

fun initKoinIOS() = initKoin()

internal val mainModule = module {
    factory<MainViewModel> {
        MainViewModel(
            store = get(),
            stateMapper = get(named("MainUIStateMapper"))
        )
    }

    factory<Mapper<MainStore.State, UIMainState>>(named("MainUIStateMapper")) {
        MainUIStateMapper()
    }

    factory<MainStore>() {
        MainStoreFactory(
            storeFactory = get(),
            repo = get(),
            webSocketService = get()
        ).create()
    }

    factory<StoreFactory> {
        val logger = object : Logger {
            override fun log(text: String) {
                println(text)
            }
        }
        LoggingStoreFactory(DefaultStoreFactory(), logger = logger)
    }
}

internal val authModule = module {
    factory<AuthViewModel> {
        AuthViewModel(
            store = get(),
            //mapper = get(named("AuthUIStateMapper"))
        )
    }

//    factory<Mapper<AuthStoreFactory.AuthStore.State, UIAuthState>>(named("AuthUIStateMapper")) {
//        UIAuthStateMapper()
//    }

    factory<AuthStore>() {
        AuthStoreFactory(
            storeFactory = get(),
            authService = get()
        ).create()
    }
}

fun services(
) = module {
    factory<PostLoader>() {
        PostLoader()
    }

    factory<WebSocketService> {
        WebSocketServiceImpl()
    }

    factory<AuthService> {
        AuthServiceImpl(setting = get())
    }
}

expect fun settingsModule(): Module