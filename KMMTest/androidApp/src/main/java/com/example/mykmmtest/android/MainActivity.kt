package com.example.mykmmtest.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mykmmtest.Models.Post
import com.example.mykmmtest.Storiess.Auth.AuthStore
import com.example.mykmmtest.Storiess.Auth.AuthViewModel
import com.example.mykmmtest.Storiess.Main.Presentation.UIMainState
import com.example.mykmmtest.Storiess.Main.ViewModel.MainViewModel
import com.example.mykmmtest.android.uielements.TextFieldView
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val authViewModel by viewModel<AuthViewModel>()
    private val mainViewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                NavigationController(
                    navController = navController,
                    mainViewModel,
                    authViewModel
                )
            }
        }
    }
}

@Composable
fun NavigationController(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    authViewModel: AuthViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "Auth"
    ) {
        composable("Auth") {
            val state by authViewModel.state.collectAsState()

            if (state.isSuccess) {
                navController.navigate("Main")
            }

            AuthView(
                state,
                navController,
                authViewModel::acceptNickname,
                authViewModel::acceptPhoneNumber,
                authViewModel::acceptPassword,
                authViewModel::trySignUp
            )
        }
        composable("Main") {
            val state by mainViewModel.state.collectAsState()
            MainView(state, mainViewModel::tapSendMessage)
        }
    }
}

@Composable
fun AuthView(
    state: AuthStore.UIAuthState,
    navController: NavHostController,
    nicknameValueChanged: (String) -> Unit,
    phoneNumberValueChanged: (String) -> Unit,
    passwordValueChanged: (String) -> Unit,
    didTapSignInButton: () -> Unit
) {
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
            Text(
                "Authorization",
                modifier = Modifier.padding(bottom = 26.dp),
                fontSize = 36.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Blue
            )

            TextFieldView(
                text = state.nickname.text,
                isError = state.nickname.isError,
                isValid = state.nickname.isValid,
                onValueChange = {
                    nicknameValueChanged(it)
                })

            TextFieldView(
                text = state.phoneNumberState.text,
                isError = state.phoneNumberState.isError,
                isValid = state.phoneNumberState.isValid,
                onValueChange = {
                    phoneNumberValueChanged(it)
                })

            TextFieldView(
                text = state.passwordState.text,
                isError = state.passwordState.isError,
                isValid = state.passwordState.isValid,
                onValueChange = {
                    passwordValueChanged(it)
                })

            Button(
                onClick = { didTapSignInButton() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign in")
            }
        }
    }
}

@Composable
fun MainView(
    state: UIMainState,
    onSendMessage: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.error
    ) {
        when {
            state.isLoading -> CircularProgressIndicator()
            state.posts != null -> PostsView(
                state.posts!!,
                onSendMessage
            )
        }
    }
}

@Composable
fun PostsView(
    posts: List<Post>,
    onSendMessage: (String) -> Unit
) {

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            Modifier.fillMaxSize()
        ) {
            posts.forEach {
                item {
                    Text(
                        it.title,
                        color = MaterialTheme.colors.primary
                    )

                    Text(
                        it.body,
                        color = MaterialTheme.colors.secondary
                    )
                }
            }
        }

        Button(
            onClick = { onSendMessage("dsdsddsds") },
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Text("Tap")
        }
    }
}

@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        val navController = rememberNavController()
        AuthView(
            state = AuthStore.UIAuthState(),
            navController = navController,
            {},
            {},
            {},
            {}
        )
    }
}