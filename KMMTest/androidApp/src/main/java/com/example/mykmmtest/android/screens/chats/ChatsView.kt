package com.example.mykmmtest.android.screens.chats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import chats.chatsmain.Chats
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.example.mykmmtest.MR
import com.example.mykmmtest.android.NavBar

@Composable
fun ChatsView(
    chats: Chats,
    onHideBottomBar: (Boolean) -> Unit
) {
    val state by chats.chats.subscribeAsState()

    NavBar(
        title = MR.strings.chats_screen_title.getString(LocalContext.current)
    ) {
        Box(
            Modifier.fillMaxSize()
        ) {
            if (state.isLoading) {
                Text("Loading")
            }

            state.errorMessage?.let {
                Text(it)
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                items(items = state.chats) {
                    ChatCellView(chatUnit = it, click = {
                        onHideBottomBar(false)
                        chats.openChat(it)
                    })
                }
            }
        }
    }
}
