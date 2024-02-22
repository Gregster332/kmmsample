package com.example.mykmmtest.android.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.example.mykmmtest.MR
import com.example.mykmmtest.stories.tab.Tab
import com.example.mykmmtest.stories.tab.Tabs

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabView(tab: Tab) {
    val state by tab.children.subscribeAsState()
    var showBottomBar by rememberSaveable {
        mutableStateOf(true)
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomAppBar {
                    NavigationBar {
                        NavigationBarItem(
                            selected = state.selectedTab == Tabs.MAIN_PAGE,
                            label = { Text("MainPage") },
                            onClick = { tab.changeTab(Tabs.MAIN_PAGE) },
                            icon = {
                                Image(
                                    painter = painterResource(id = MR.images.xmark.drawableResId),
                                    contentDescription = ""
                                )
                            }
                        )

                        NavigationBarItem(
                            selected = state.selectedTab == Tabs.SETTINGS,
                            label = { Text("Settings") },
                            onClick = { tab.changeTab(Tabs.SETTINGS) },
                            icon = {
                                Image(
                                    painter = painterResource(id = MR.images.xmark.drawableResId),
                                    contentDescription = ""
                                )
                            }
                        )
                    }
                }
            }
        }
    ) {
       // NavBar(title = MR.strings.auth_screen_title.getString(LocalContext.current)) {
            when (state.selectedTab) {
                Tabs.MAIN_PAGE -> {
                    MainPageView(
                        mainPages = state.mainPage.instance
                    ) {
                        showBottomBar = it
                    }
                }

                Tabs.SETTINGS -> {
                    Text("Settings")
                }
            }
       // }
    }
}
