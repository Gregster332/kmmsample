package com.example.mykmmtest.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.isFront
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.example.chats.api.ChatsComponent
import com.example.mykmmtest.MR
import com.example.mykmmtest.Storiess.Auth.AuthComponent
import com.example.mykmmtest.Storiess.Splash.Splash
import com.example.mykmmtest.Storiess.Splash.SplashComponent
import com.example.mykmmtest.android.uielements.TextFieldView

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

    private fun makeSplash(componentContext: ComponentContext): SplashComponent =
        SplashComponent(
            componentContext
        )
}

@Composable
fun SplashViewContent(component: SplashComponent) {
    val stack by component.childStack.subscribeAsState()

    NavBar {
        Children(
            stack = stack,
            animation = stackAnimation { from, to, dir ->
                if (dir.isFront) {
                    slide()
                } else {
                    slide()
                }
            }
        ) {
            when (val child = it.instance) {
                is Splash.Child.Auth -> AuthView(child.component)
                is Splash.Child.Chats -> ChatsView(child.component)
                else -> Text("")
            }
        }
    }
}

@Composable
fun AuthView(
    component: AuthComponent
) {
    val state by component.state.subscribeAsState()

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TextFieldView(
                text = state.nickname.text,
                isError = state.nickname.isError,
                isValid = state.nickname.isValid,
                title = MR.strings.nickname_field_title.getString(LocalContext.current),
                onValueChange = {
                    component.validateNickname(it)
                })

            TextFieldView(
                text = state.phoneNumberState.text,
                isError = state.phoneNumberState.isError,
                isValid = state.phoneNumberState.isValid,
                title = MR.strings.phone_field_title.getString(LocalContext.current),
                onValueChange = {
                    component.validatePhone(it)
                })

            TextFieldView(
                text = state.passwordState.text,
                isError = state.passwordState.isError,
                isValid = state.passwordState.isValid,
                title = MR.strings.password_field_title.getString(LocalContext.current),
                onValueChange = {
                    component.validatePassword(it)
                })

            Button(
                onClick = { component.authWithPassword() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign in")
            }
        }
    }
}

@Composable
fun ChatsView(component: ChatsComponent) {
    val state by component.chats.subscribeAsState()

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        Text("Chats")
    }
}

@Composable
fun NavBar(content: @Composable (PaddingValues) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = colorResource(
                    id = MR.colors.backgroundColor.resourceId
                ),
                title = {
                    Text("Authorization")
                }
            )
        }
    ) { paddings ->
        content(paddings)
    }
}

@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {

    }
}