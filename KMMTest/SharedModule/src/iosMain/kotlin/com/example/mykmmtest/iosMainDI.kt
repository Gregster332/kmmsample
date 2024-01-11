package com.example.mykmmtest

import com.example.mykmmtest.DI.initKoin
import com.example.mykmmtest.Storiess.Auth.AuthViewModel
import com.example.mykmmtest.Storiess.Main.ViewModel.MainViewModel
import com.example.mykmmtest.Storiess.Splash.SplashViewModel
import com.russhwolf.settings.KeychainSettings
import com.russhwolf.settings.Settings
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.qualifier.named
import org.koin.dsl.module

class IosMainDI: KoinComponent {
    fun mainViewModel(): MainViewModel = get()
    fun authViewModel(): AuthViewModel = get()
    fun splashViewModel(): SplashViewModel = get()
}

fun initKoinIOS(settings: KeychainSettings) = initKoin(
    appDeclaration = module {
        single<Settings>(named("default_settings")) {
            settings
        }
    }
)