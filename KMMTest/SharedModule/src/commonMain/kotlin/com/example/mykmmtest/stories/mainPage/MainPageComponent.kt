package com.example.mykmmtest.stories.mainPage

import chats.chat.Chat
import chats.chat.ChatComponent
import chats.chatsmain.ChatsComponent
import com.arkivanov.decompose.Child
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.children.ChildNavState
import com.arkivanov.decompose.router.children.NavState
import com.arkivanov.decompose.router.children.SimpleChildNavState
import com.arkivanov.decompose.router.children.SimpleNavigation
import com.arkivanov.decompose.router.children.children
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.essenty.lifecycle.doOnCreate
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.example.core.koin.ComponentKoinContext
import com.example.core.koin.coreModules
import com.example.corenetwork.di.coreNetworkModule
import com.example.corenetwork.model.chats.ChatUnit
import com.example.mykmmtest.di.services
import com.example.mykmmtest.di.storeFactoryModule
import com.example.searchlist.SearchListComponent
import kotlinx.serialization.Serializable

@Serializable
sealed class MainPageConfiguration {
    @Serializable
    data object Main : MainPageConfiguration()

    @Serializable
    data object Search : MainPageConfiguration()
}

@Serializable
sealed interface StackConfig {
    @Serializable
    data object Base : StackConfig
    @Serializable
    data class Chat(val chat: ChatUnit): StackConfig
}

interface MainPages {
    val children: Value<Children>
    val stack: Value<ChildStack<*, StackChild>>

    fun list(open: Boolean)
    fun popStack()

    data class Children(
        val mainChild: Child.Created<*, ChatsComponent>,
        val searchListChild: Child.Created<*, SearchListComponent>?,
    )

    sealed class StackChild {
        data object Def: StackChild()
        data class ChatMain(val chat: ChatComponent): StackChild()
    }
}

class MainPagesComponent(
    componentContext: ComponentContext
) : MainPages, ComponentContext by componentContext {
    private val koinContext =
        instanceKeeper.getOrCreate {
            ComponentKoinContext()
        }

    private val scope =
        koinContext.getOrCreateKoinScope(
            coreModules + listOf(coreNetworkModule, storeFactoryModule, services()),
        )

    private val navigation = SimpleNavigation<(NavigationState) -> NavigationState>()
    private val stackNavigation = StackNavigation<StackConfig>()

    init {
        lifecycle.doOnCreate { println("CREATE MAIN BASE") }
        lifecycle.doOnDestroy { println("desrt MAIN BASE") }
    }

    override val children: Value<MainPages.Children> =
        children(
            source = navigation,
            stateSerializer = NavigationState.serializer(),
            key = "main_page",
            initialState = ::NavigationState,
            stateMapper = { navState, children ->
                @Suppress("UNCHECKED_CAST")
                MainPages.Children(
                    mainChild = children.first { it.instance is ChatsComponent } as Child.Created<*, ChatsComponent>,
                    searchListChild = children.find { it.instance is SearchListComponent } as? Child.Created<*, SearchListComponent>,
                )
            },
            navTransformer = { navState, event -> event(navState) },
            childFactory = ::child,
        )

    override val stack: Value<ChildStack<*, MainPages.StackChild>> = childStack(
        stackNavigation,
        serializer = StackConfig.serializer(),
        initialConfiguration = StackConfig.Base,
        handleBackButton = true,
        childFactory = ::child
    )

    override fun list(open: Boolean) {
        navigation.navigate { it.copy(isSearchOpen = open) }
    }

    override fun popStack() {
        stackNavigation.pop()
    }

    private fun child(config: MainPageConfiguration, componentContext: ComponentContext): Any = when (config) {
            is MainPageConfiguration.Main ->
                ChatsComponent(
                    componentContext = componentContext,
                    storeFactory = scope.get(),
                    chatsApi = scope.get(),
                    onChatClicked = {
                        stackNavigation.push(StackConfig.Chat(it))
                    },
                )

            is MainPageConfiguration.Search ->
                SearchListComponent(
                    componentContext = componentContext,
                    storeFactory = scope.get(),
                    usersApi = scope.get(),
                    chatsApi = scope.get(),
                    db = scope.get(),
                    onTapUser = {
                        children.value.mainChild.instance.tryLoadChats()
                    },
                )
        }

    private fun child(config: StackConfig, componentContext: ComponentContext): MainPages.StackChild = when(config) {
        is StackConfig.Base -> MainPages.StackChild.Def
        is StackConfig.Chat -> MainPages.StackChild.ChatMain(
            ChatComponent(
                componentContext,
                scope.get(),
                scope.get(),
                scope.get(),
                scope.get(),
                config.chat
            ) {
                stackNavigation.pop()
            }
        )
    }

    @Serializable
    private data class NavigationState(
        val isSearchOpen: Boolean = false,
    ) : NavState<MainPageConfiguration> {
        override val children: List<ChildNavState<MainPageConfiguration>> by lazy {
            listOfNotNull(
                SimpleChildNavState(
                    MainPageConfiguration.Main,
                    if (!isSearchOpen) ChildNavState.Status.STARTED else ChildNavState.Status.RESUMED,
                ),
                if (isSearchOpen) {
                    SimpleChildNavState(
                        MainPageConfiguration.Search,
                        ChildNavState.Status.CREATED,
                    )
                } else {
                    null
                },
            )
        }
    }
}
