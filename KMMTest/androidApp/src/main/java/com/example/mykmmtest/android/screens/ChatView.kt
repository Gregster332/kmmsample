package com.example.mykmmtest.android.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chats.chat.Chat
import chats.chat.PreviewChatComponent
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.example.mykmmtest.MR

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatView(chat: Chat, onHideBottomBar: (Boolean) -> Unit) {

    val chats by chat.currentMessages.subscribeAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor =
                colorResource(
                    id = MR.colors.backgroundColor.resourceId,
                ),
                title = {
                    Text("Chat")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        onHideBottomBar(true)
                        chat.close()
                    }) {
                        Icon(
                            painter = painterResource(id = MR.images.xmark.drawableResId),
                            contentDescription = ""
                        )
                    }
                }
            )
        }
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.Bottom),
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(bottom = 8.dp)
        ) {
            items(chats.messages) {
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(
                                colorResource(id = MR.colors.successStateMainColor.resourceId)
                            )
                    )

                    Column(
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .padding(end = 24.dp)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 10.dp,
                                    bottomStart = 10.dp,
                                    topEnd = 10.dp,
                                    bottomEnd = 10.dp
                                )
                            )
                            .background(Color.Blue.copy(0.3f))
                            .padding(8.dp)

                    ) {
                        Text(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            text = "Nickname"
                        )
                        Text(it)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ChatViewPreview() {
    ChatView(chat = PreviewChatComponent(), onHideBottomBar = {})
}