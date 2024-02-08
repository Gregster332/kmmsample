package com.example.mykmmtest.Storiess.MainPage

import com.arkivanov.decompose.Child
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.children.ChildNavState
import com.arkivanov.decompose.router.children.NavState
import com.arkivanov.decompose.router.children.SimpleChildNavState
import com.arkivanov.decompose.router.children.SimpleNavigation
import com.arkivanov.decompose.router.children.children
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.example.chats.api.ChatsComponent
import com.example.core.koin.ComponentKoinContext
import com.example.corenetwork.api.Chats.ChatsApi
import com.example.corenetwork.api.Chats.WebSocketService
import com.example.corenetwork.api.Users.UsersApi
import com.example.corenetwork.di.coreNetworkModule
import com.example.mykmmtest.DI.services
import com.example.mykmmtest.DI.storeFactoryModule
import com.example.searchlist.SearchListComponent
import org.koin.core.component.KoinComponent
import kotlinx.coroutines.flow.MutableSharedFlow

sealed class MainPageConfiguration: Parcelable {
    @Parcelize
    data object Main: MainPageConfiguration()
    @Parcelize
    data object Search: MainPageConfiguration()
}

interface MainPages {
    val children: Value<Children>

    fun list(open: Boolean)

    data class Children(
        val mainChild: Child.Created<*, ChatsComponent>,
        val searchListChild: Child.Created<*, SearchListComponent>?
    )
}

class MainPagesComponent(
    componentContext: ComponentContext,
    private val chatsApi: ChatsApi,
    private val usersApi: UsersApi
): MainPages, ComponentContext by componentContext, KoinComponent {

    private val koinContext = instanceKeeper.getOrCreate {
        ComponentKoinContext()
    }

    private val scope = koinContext.getOrCreateKoinScope(
        listOf(coreNetworkModule, storeFactoryModule, services())
    )

    private val navigation = SimpleNavigation<(NavigationState) -> NavigationState>()
    private val eventsFlow = MutableSharedFlow<String>()

    override val children: Value<MainPages.Children> = children(
        source = navigation,
        key = "main_page",
        initialState = ::NavigationState,
        stateMapper = { navState, children ->
            @Suppress("UNCHECKED_CAST")
            MainPages.Children(
                mainChild = children.first { it.instance is ChatsComponent } as Child.Created<*, ChatsComponent>,
                searchListChild = children.find { it.instance is SearchListComponent } as? Child.Created<*, SearchListComponent>
            )
        },
        navTransformer = { navState, event -> event(navState) },
        childFactory = ::child
    )

    override fun list(open: Boolean) {
        navigation.navigate { it.copy(isSearchOpen = open) }
    }

    private fun child(config: MainPageConfiguration, componentContext: ComponentContext): Any = when(config) {
        is MainPageConfiguration.Main -> ChatsComponent(
            componentContext = componentContext,
            storeFactory = scope.get(),
            chatsApi = chatsApi
        )

        is MainPageConfiguration.Search -> SearchListComponent(
            componentContext = componentContext,
            storeFactory = scope.get(),
            usersApi = usersApi,
            chatsApi = chatsApi,
            db = scope.get(),
            onTapUser = {
                println("dsdsdsdsdsds")
                children.value.mainChild.instance.tryLoadChats()
            }
        )
    }

    private data class NavigationState(
        val isSearchOpen: Boolean = false
    ): NavState<MainPageConfiguration>, Parcelable {
        override val children: List<ChildNavState<MainPageConfiguration>> by lazy {
            val list = listOfNotNull(
                SimpleChildNavState(MainPageConfiguration.Main, if (!isSearchOpen) ChildNavState.Status.ACTIVE else ChildNavState.Status.INACTIVE),
                if (isSearchOpen) SimpleChildNavState(MainPageConfiguration.Search, ChildNavState.Status.ACTIVE) else null
            )
            println("LIST: $list")
            list
        }
    }
}