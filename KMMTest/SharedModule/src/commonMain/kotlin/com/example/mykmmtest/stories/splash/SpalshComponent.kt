package com.example.mykmmtest.stories.splash

import com.arkivanov.decompose.Child
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.children.ChildNavState
import com.arkivanov.decompose.router.children.NavState
import com.arkivanov.decompose.router.children.SimpleChildNavState
import com.arkivanov.decompose.router.children.SimpleNavigation
import com.arkivanov.decompose.router.children.children
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.essenty.lifecycle.subscribe
import com.arkivanov.mvikotlin.core.binder.Binder
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.example.authentication.Auth.SignUpComponent
import com.example.core.koin.ComponentKoinContext
import com.example.core.koin.coreModules
import com.example.corenetwork.di.coreNetworkModule
import com.example.mykmmtest.di.platformDependenciesModule
import com.example.mykmmtest.di.services
import com.example.mykmmtest.di.storeFactoryModule
import com.example.mykmmtest.stories.debugMenu.DebugMenuComponent
import com.example.mykmmtest.stories.tab.TabComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable

class SplashComponent(
    componentContext: ComponentContext,
) : Splash, ComponentContext by componentContext {
    private val koinContext =
        instanceKeeper.getOrCreate {
            ComponentKoinContext()
        }

    private val scope = koinContext.getOrCreateKoinScope(
        coreModules + listOf(
            coreNetworkModule,
            storeFactoryModule,
            services(),
            platformDependenciesModule()
        ),
    )

    private val store =
        instanceKeeper.getStore {
            SplashStoreFactory(
                storeFactory = scope.get(),
                authService = scope.get(),
                localCache = scope.get(),
                scope.get(),
                didSensorSignal = {
                    sheetNavigation.activate(DialogConfiguration.DebugMenu)
                }
            ).create()
        }

    private val navigation = SimpleNavigation<(SplashNavState) -> SplashNavState>()
    private val sheetNavigation = SlotNavigation<DialogConfiguration>()
    private lateinit var binder: Binder

    override val child = children(
        navigation,
        stateSerializer = SplashNavState.serializer(),
        key = "splash_main",
        initialState = ::SplashNavState,
        stateMapper = { _, children ->
            @Suppress("UNCHECKED_CAST")
            Splash.SplashChild(
                tabChild = children.find { it.instance is TabComponent } as? Child.Created<*, TabComponent>,
                authChild = children.find { it.instance is SignUpComponent } as? Child.Created<*, SignUpComponent>,
            )
        },
        navTransformer = { state, event -> event(state) },
        childFactory = ::createChild
    )

    override val sheet: Value<ChildSlot<*, Splash.SheetChild>> =
        childSlot(
            source = sheetNavigation,
            serializer = null,
            handleBackButton = true,
            childFactory = ::createChild,
        )

    init {
        lifecycle.subscribe(
            onStart = {
                binder =
                    bind(Dispatchers.Main.immediate) {
                        store.states.map { it } bindTo (::acceptState)
                    }
                binder.start()
            },
            onStop = {
                binder.stop()
                store.dispose()
            },
        )
    }

    override fun didShakeDevice() {

    }

    private fun createChild(config: Configuration, componentContext: ComponentContext): Any = when (config) {
        is Configuration.MainPage -> TabComponent(
            componentContext = componentContext
        )
        is Configuration.Auth -> SignUpComponent(
            componentContext = componentContext,
            storeFactory = scope.get(),
            authApi = scope.get(),
            settings = scope.get(),
            onSuccess = {
                navigation.navigate { it.copy(SplashStore.AuthorizeState.Autheticated) }
            }
        )
    }

    private fun createChild(config: DialogConfiguration, componentContext: ComponentContext): Splash.SheetChild =
        when (config) {
            is DialogConfiguration.DebugMenu ->
                Splash.SheetChild.DebugMenu(
                    component =
                    DebugMenuComponent(
                        componentContext,
                        onClose = {
                            sheetNavigation.dismiss()
                        },
                    ),
                )
        }

    private fun acceptState(state: SplashStore.UISplashState) = when (state.authState) {
        is SplashStore.AuthorizeState.NeedRefreshToken -> {
            store.accept(SplashStore.Intent.RefreshToken)
        }
        is SplashStore.AuthorizeState.Autheticated -> {
            navigation.navigate { it.copy(state.authState) }
            //navigation.push(Configuration.MainPage)
        }
        is SplashStore.AuthorizeState.Reauth -> {
            navigation.navigate { it.copy(state.authState) }
            //navigation.push(Configuration.Auth)
        }
        else -> {}
    }

    @Serializable
    private sealed class Configuration {
        @Serializable
        data object MainPage : Configuration()

        @Serializable
        data object Auth : Configuration()
    }

    @Serializable
    private sealed class DialogConfiguration {
        @Serializable
        data object DebugMenu : DialogConfiguration()
    }

    @Serializable
    private data class SplashNavState(
        private val authState: SplashStore.AuthorizeState = SplashStore.AuthorizeState.NotSet
    ): NavState<Configuration> {
        override val children: List<ChildNavState<Configuration>> by lazy {
            listOfNotNull(
                if (authState is SplashStore.AuthorizeState.Autheticated) {
                    SimpleChildNavState(
                        Configuration.MainPage,
                        ChildNavState.Status.CREATED
                    )
                } else {
                       null
                },
                if (authState is SplashStore.AuthorizeState.Reauth) {
                    SimpleChildNavState(
                        Configuration.Auth,
                        ChildNavState.Status.CREATED
                    )
                } else {
                    null
                }
            )
        }
    }
}

interface Splash {
    val child: Value<SplashChild>
    val sheet: Value<ChildSlot<*, SheetChild>>

    fun didShakeDevice()

    data class SplashChild(
        val tabChild: Child.Created<*, TabComponent>? = null,
        val authChild: Child.Created<*, SignUpComponent>? = null
    )

    //@Serializable
    sealed class SheetChild {
        data class DebugMenu(
            val component: DebugMenuComponent,
            val isFullscreen: Boolean = true,
        ) : SheetChild()
    }
}
