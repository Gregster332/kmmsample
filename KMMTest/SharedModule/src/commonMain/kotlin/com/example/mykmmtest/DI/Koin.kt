package com.example.mykmmtest.DI

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.logging.logger.Logger
import com.arkivanov.mvikotlin.logging.store.LoggingStoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.example.mykmmtest.Services.AuthService.AuthService
import com.example.mykmmtest.Services.AuthService.AuthServiceImpl
import com.example.mykmmtest.Services.PostLoader
import com.example.mykmmtest.Services.SecurePersistant.SecurePersistant
import com.example.mykmmtest.Services.SecurePersistant.SecurePersistantImpl
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
import com.example.mykmmtest.Storiess.Splash.SplashStore
import com.example.mykmmtest.Storiess.Splash.SplashStoreFactory
import com.example.mykmmtest.Storiess.Splash.SplashViewModel
import com.example.mykmmtest.Utils.Mapper
import com.russhwolf.settings.Settings
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(
    block: KoinAppDeclaration = {},
    appDeclaration: Module
) {
    startKoin {
        block()
        modules(
            appDeclaration,
            platformModule,
            settingsModule(),
            services(),
            storeFactoryModule,
            splashModule,
            mainModule,
            authModule
        )
    }
}

internal var storeFactoryModule = module {
    factory<StoreFactory> {
        val logger = object : Logger {
            override fun log(text: String) {
                println(text)
            }
        }
        LoggingStoreFactory(DefaultStoreFactory(), logger = logger)
    }
}

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
}

internal val authModule = module {
    factory<AuthViewModel> {
        AuthViewModel(
            store = get()
        )
    }

    factory<AuthStore>() {
        AuthStoreFactory(
            storeFactory = get(),
            authService = get()
        ).create()
    }
}

internal val splashModule = module {
    factory<SplashViewModel> {
        SplashViewModel(get())
    }

    factory<SplashStore> {
        SplashStoreFactory(get(), get()).create()
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

fun settingsModule() = module {
    single<SecurePersistant> {
        SecurePersistantImpl(get(named("default_settings")))
    }
}

expect var platformModule: Module