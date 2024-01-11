package com.example.mykmmtest.Storiess.Main

import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.mykmmtest.Services.Chat
import com.example.mykmmtest.Services.ChatUnit
import com.example.mykmmtest.Services.PostLoader
import com.example.mykmmtest.Services.Result
import com.example.mykmmtest.Services.WebSocketService
import com.example.mykmmtest.Services.WsMessage
import com.example.mykmmtest.Storiess.Main.Factory.MainStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random

internal abstract class BaseExecutor<in Intent : Any, in Action : Any, in State : Any, Message : Any, Label : Any>(
    mainContext: CoroutineContext = Dispatchers.Main,
) : CoroutineExecutor<Intent, Action, State, Message, Label>(mainContext = mainContext) {
    final override fun executeIntent(intent: Intent, getState: () -> State) {
        CoroutineScope(Dispatchers.Main).launch {
            suspendExecuteIntent(intent, getState)
        }
    }

    override fun executeAction(action: Action, getState: () -> State) {
        CoroutineScope(Dispatchers.Main).launch {
            suspendExecuteAction(action, getState)
        }
    }

    open suspend fun suspendExecuteIntent(intent: Intent, getState: () -> State) {}
    open suspend fun suspendExecuteAction(action: Action, getState: () -> State) {}
}

internal class MainExecutor(
    //private val webSocketService: WebSocketService
    private val postLoader: PostLoader
) : BaseExecutor<MainStore.Intent, Nothing, MainStore.State, MainStoreFactory.Message, Nothing>() {

    override suspend fun suspendExecuteIntent(
        intent: MainStore.Intent,
        getState: () -> MainStore.State,
    ) = when (intent) {
        is MainStore.Intent.LoadChats -> fetchAllChats()
        is MainStore.Intent.OnCreateNewChat -> createNewChat(intent.name)
        is MainStore.Intent.OnTapSendMessage -> sendMessage(intent.string)
    }

    private fun onConnect() {
//        webSocketService.connect()
//
//        webSocketService.messageListenerBlock = {
//            try {
//                val message = Json.decodeFromString<WsMessage>(it)
//                dispatch(MainStoreFactory.Message.DidReceiveData(message))
//            } catch(e: Exception) {
//                print(e)
//            }
//        }
//
//        webSocketService.onOpenBlock = {
//            println("Socket open")
//        }
//
//        webSocketService.onCloseBlock = {
//            println("Socket closed")
//        }
//
//        webSocketService.onFailureBlock = {
//            println(it)
//        }
    }

    private fun sendMessage(message: String) {
        //webSocketService.send(message)
    }

    private suspend fun createNewChat(name: String) {
        postLoader.fetchAllPosts(
            Chat(Random.nextInt(), name, "fd")
        )
    }

    private suspend fun fetchAllChats() {
        val chats = postLoader.getAllUserChats()
        dispatch(MainStoreFactory.Message.OnChatsLoaded(chats))
    }
}