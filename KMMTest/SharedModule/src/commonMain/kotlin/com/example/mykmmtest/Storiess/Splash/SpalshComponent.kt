package com.example.mykmmtest.Storiess.Splash

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.essenty.lifecycle.subscribe
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.arkivanov.mvikotlin.core.binder.Binder
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.example.apptheme.AppThemeComponent
import com.example.authentication.Auth.SignUpComponent
import com.example.core.koin.ComponentKoinContext
import com.example.corenetwork.di.coreNetworkModule
import com.example.mykmmtest.DI.services
import com.example.mykmmtest.DI.storeFactoryModule
import com.example.mykmmtest.Storiess.MainPage.MainPagesComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map

class SplashComponent(
    componentContext: ComponentContext,
): Splash, ComponentContext by componentContext {
    private val koinContext = instanceKeeper.getOrCreate {
        ComponentKoinContext()
    }

    private val scope = koinContext.getOrCreateKoinScope(
        listOf(coreNetworkModule, storeFactoryModule, services())
    )

    private val store = instanceKeeper.getStore {
        SplashStoreFactory(
            storeFactory = scope.get(),
            authService = scope.get(),
            localCache = scope.get(),
            onAuthUser = {
            }
        ).create()
    }

    private val navigation = StackNavigation<Configuration>()
    private val sheetNavigation = SlotNavigation<DialogConfiguration>()
    private lateinit var binder: Binder

    override val childStack: Value<ChildStack<*, Splash.Child>> = childStack(
        source = navigation,
        initialConfiguration = Configuration.Main,
        handleBackButton = true,
        childFactory = ::createChild
    )

    override val sheet: Value<ChildSlot<*, Splash.SheetChild>> = childSlot(
        source = sheetNavigation,
        handleBackButton = true,
        childFactory = ::createChild
    )

    init {
        lifecycle.subscribe(
            onStart = {
                binder = bind(Dispatchers.Main.immediate) {
                    store.states.map { it } bindTo (::acceptState)
                }
                binder.start()
            },
            onStop = {
                binder.stop()
                store.dispose()
            }
        )
    }

    override fun onBackPressed() {
        navigation.pop()
    }

    override fun navigateAuth() {
        navigation.push(Configuration.Chats)
    }

    override fun dismiss() {
        sheetNavigation.dismiss()
    }

    private fun createChild(config: Configuration, componentContext: ComponentContext): Splash.Child =
        when (config) {
            is Configuration.Main -> Splash.Child.Main
            is Configuration.Chats -> Splash.Child.ChatsMain(
                component = MainPagesComponent(
                    componentContext = componentContext,
                    chatsApi = scope.get(),
                    usersApi = scope.get()
                )
            )
            is Configuration.Auth -> Splash.Child.Auth(
                SignUpComponent(
                    componentContext = componentContext,
                    storeFactory = scope.get(),
                    authApi = scope.get(),
                    settings = scope.get(),
                    onAppTheme = {
                        sheetNavigation.activate(DialogConfiguration.AppTheme)
                    },
                    onSuccess = {
                        navigation.push(Configuration.Chats)
                    }
                )
            )
        }

    private fun createChild(config: DialogConfiguration, componentContext: ComponentContext): Splash.SheetChild =
        when (config) {
            is DialogConfiguration.AppTheme -> Splash.SheetChild.AppTheme(
                component = AppThemeComponent(
                    componentContext,
                    onDismiss = {
                        sheetNavigation.dismiss()
                    }
                )
            )
        }

    private fun acceptState(state: SplashStore.UISplashState) = when(state.authState) {
        is SplashStore.AuthorizeState.NeedRefreshToken -> {
            store.accept(SplashStore.Intent.RefreshToken)
        }
        is SplashStore.AuthorizeState.Autheticated -> {
            navigation.push(Configuration.Chats)
        }
        is SplashStore.AuthorizeState.Reauth -> {
            navigation.push(Configuration.Auth)
        }
        else -> {}
    }

    private sealed class Configuration: Parcelable {
        @Parcelize
        data object Main: Configuration()
        @Parcelize
        data object Chats: Configuration()
        @Parcelize
        data object Auth: Configuration()
    }

    private sealed class DialogConfiguration: Parcelable {
        @Parcelize object AppTheme: DialogConfiguration()
    }
}

interface Splash {
    val childStack: Value<ChildStack<*, Child>>
    val sheet: Value<ChildSlot<*, SheetChild>>

    fun onBackPressed()
    fun navigateAuth()
    fun dismiss()

    sealed class Child {
        data object Main: Child()
        class ChatsMain(val component: MainPagesComponent): Child()
        class Auth(val component: SignUpComponent): Child()
    }

    sealed class SheetChild {
        class AppTheme(val component: AppThemeComponent): SheetChild()
    }
}