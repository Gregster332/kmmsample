package com.example.mykmmtest.stories.tab

import com.arkivanov.decompose.Child
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.children.ChildNavState
import com.arkivanov.decompose.router.children.NavState
import com.arkivanov.decompose.router.children.SimpleChildNavState
import com.arkivanov.decompose.router.children.SimpleNavigation
import com.arkivanov.decompose.router.children.children
import com.arkivanov.decompose.value.Value
import com.example.apptheme.SettingsPageComponent
import com.example.mykmmtest.stories.mainPage.MainPages
import com.example.mykmmtest.stories.mainPage.MainPagesComponent
import kotlinx.serialization.Serializable

interface Tab {
    val children: Value<TabChild>

    data class TabChild(
        val mainPage: Child.Created<*, MainPages>,
        val settings: Child.Created<*, SettingsPageComponent>
    )
}

enum class Tabs {
    MAIN_PAGE, SETTINGS
}

class TabComponent(
    componentContext: ComponentContext
): Tab, ComponentContext by componentContext {
    private val navigation = SimpleNavigation<(TabComponentNavState) -> TabComponentNavState>()

    override val children: Value<Tab.TabChild> = children(
        navigation,
        key = "Tab_page",
        stateSerializer = TabComponentNavState.serializer(),
        initialState = ::TabComponentNavState,
        stateMapper = { s, child ->
            @Suppress("UNCHECKED_CAST")
            Tab.TabChild(
                mainPage = child.first { it.instance is MainPagesComponent } as Child.Created<*, MainPagesComponent>,
                settings = child.first { it.instance is SettingsPageComponent } as Child.Created<*, SettingsPageComponent>,
            )
        },
        navTransformer = { navState, event -> event(navState) },
        childFactory = ::child
    )

    private fun child(config: TabConfig, componentContext: ComponentContext): Any = when (config) {
        is TabConfig.MainPage -> MainPagesComponent(
            componentContext
        )
        is TabConfig.Settings -> SettingsPageComponent(
            componentContext,

        )
    }

    @Serializable
    sealed class TabConfig {
        @Serializable
        data object MainPage : TabConfig()
        @Serializable
        data object Settings : TabConfig()
    }

    @Serializable
    private data class TabComponentNavState(
        val selectedTab: Tabs = Tabs.MAIN_PAGE
    ): NavState<TabConfig> {
        override val children: List<ChildNavState<TabConfig>> by lazy {
            listOfNotNull(
                SimpleChildNavState(
                    TabConfig.MainPage,
                    if (selectedTab == Tabs.MAIN_PAGE) ChildNavState.Status.CREATED else ChildNavState.Status.RESUMED
                ),
                SimpleChildNavState(
                    TabConfig.Settings,
                    if (selectedTab == Tabs.SETTINGS) ChildNavState.Status.CREATED else ChildNavState.Status.RESUMED
                ),
            )
        }
    }
}