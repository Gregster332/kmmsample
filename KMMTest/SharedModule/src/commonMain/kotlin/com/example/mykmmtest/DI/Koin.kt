package com.example.mykmmtest.DI

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.logging.logger.Logger
import com.arkivanov.mvikotlin.logging.store.LoggingStoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.example.mykmmtest.Services.PostLoader
import com.example.mykmmtest.Stories.Main.Model.Factory.MainStoreFactory
import com.example.mykmmtest.Stories.Main.Model.MainStore
import com.example.mykmmtest.Stories.Main.Model.Presentation.MainUIStateMapper
import com.example.mykmmtest.Stories.Main.Model.Presentation.UIMainState
import com.example.mykmmtest.Stories.Main.Model.ViewModel.MainViewModel
import com.example.mykmmtest.Utils.Mapper
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(
            mainModule
        )
    }
}

fun initKoinIOS() = initKoin { }

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
            repo = get()
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

    factory<PostLoader>() {
        PostLoader()
    }
}