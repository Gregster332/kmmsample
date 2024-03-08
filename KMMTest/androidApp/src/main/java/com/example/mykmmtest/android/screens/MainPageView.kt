package com.example.mykmmtest.android.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.example.mykmmtest.android.screens.chats.ChatsView
import com.example.mykmmtest.stories.mainPage.MainPages

@Composable
fun MainPageView(mainPages: MainPages, onHideBottomBar: (Boolean) -> Unit) {
    val stack by mainPages.stack.subscribeAsState()
    val state by mainPages.children.subscribeAsState()

    Box {
        Column(Modifier.fillMaxSize()) {
            ChatsView(
                chats = state.mainChild.instance,
                onHideBottomBar = onHideBottomBar
            )
        }

        Children(
            stack = stack,
            animation = stackAnimation(fade())
        ) {
            when (val child = it.instance) {
                is MainPages.StackChild.ChatMain -> {
                    ChatView(chat = child.chat, onHideBottomBar) { mainPages.popStack() }
                }

                else -> {
                    Color.clear()
                }
            }
        }
    }
}