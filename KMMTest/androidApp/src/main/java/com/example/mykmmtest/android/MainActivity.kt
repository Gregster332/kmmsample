package com.example.mykmmtest.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mykmmtest.Models.Post
import com.example.mykmmtest.Storiess.Main.Presentation.UIMainState
import com.example.mykmmtest.Storiess.Main.ViewModel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val state by mainViewModel.state.collectAsState()
            MainView(state, mainViewModel::tapSendMessage)
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
        MainView(
            state = UIMainState(
                isLoading = false,
                posts = listOf(
                    Post(0L, 9L, "Helo", "hellooooo")
                )
            ),
            onSendMessage = {}
        )
    }
}
