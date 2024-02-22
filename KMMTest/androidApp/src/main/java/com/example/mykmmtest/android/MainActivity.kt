package com.example.mykmmtest.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.example.authentication.Auth.SignUpComponent
import com.example.mykmmtest.MR
import com.example.mykmmtest.android.screens.DialogContent
import com.example.mykmmtest.android.screens.SignUpView
import com.example.mykmmtest.android.screens.TabView
import com.example.mykmmtest.stories.splash.Splash
import com.example.mykmmtest.stories.splash.SplashComponent
import com.example.mykmmtest.stories.tab.TabComponent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val splash = makeSplash(defaultComponentContext())

            MyApplicationTheme {
                SplashViewContent(component = splash)
            }
        }
    }

    private fun makeSplash(
        componentContext: ComponentContext
    ): SplashComponent = SplashComponent(
            componentContext
    )
}

@Composable
fun SplashViewContent(
    component: SplashComponent
) {
    val stack by component.child.subscribeAsState()
    val debugMenu by component.sheet.subscribeAsState()

    val context = LocalContext.current

    Box {
        when (val tab = stack.tabChild?.instance) {
            is TabComponent -> {

                    TabView(tab = tab)

            }
        }

        when (val auth = stack.authChild?.instance) {
            is SignUpComponent -> {
                NavBar(title = MR.strings.auth_screen_title.getString(context)) {
                    SignUpView(component = auth)
                }
            }
        }


        debugMenu.child?.instance?.also {
            when (it) {
                is Splash.SheetChild.DebugMenu -> {
                    DialogContent(debugMenu = it.component)
                }
            }
        }
    }
}

@Composable
fun NavBar(
    title: String,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor =
                colorResource(
                    id = MR.colors.backgroundColor.resourceId,
                ),
                title = {
                    Text(title)
                },
            )
        },
    ) { paddings ->
        content(paddings)
    }
}
