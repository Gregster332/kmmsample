package com.example.mykmmtest.Services

import com.example.mykmmtest.Services.SocketServiceImpl.SocketServiceImpl
import kotlinx.serialization.Serializable

@Serializable
data class WsMessage(
    val userId: String,
    val userName: String,
    val text: String
)

internal interface WebSocketService {
    var onOpenBlock: (() -> Unit)?
    var onFailureBlock: ((Throwable) -> Unit)?
    var onCloseBlock: (() -> Unit)?
    var messageListenerBlock: ((msg: String) -> Unit)?

    fun connect()
    fun disconnect()
    fun send(msg: String)
}

internal class WebSocketServiceImpl: WebSocketService {
    override var onOpenBlock: (() -> Unit)? = null
    override var onFailureBlock: ((Throwable) -> Unit)? = null
    override var onCloseBlock: (() -> Unit)? = null
    override var messageListenerBlock: ((msg: String) -> Unit)? = null

    private val socket = SocketServiceImpl("ws://0.0.0.0:8080/chat")

    override fun connect() {
        socket.open(socketListener)
    }

    override fun disconnect() {
        socket.close()
    }
    override fun send(msg: String) {
        socket.sendMessage(msg)
    }

    private val socketListener: PlatformListener = object : PlatformListener {
        override fun onOpen() {
           onOpenBlock?.invoke()
        }
        override fun onFailure(error: Throwable) {
            onFailureBlock?.invoke(error)
        }
        override fun onMessage(msg: String) {
           messageListenerBlock?.invoke(msg)
        }

        override fun onClose() {
            onCloseBlock?.invoke()
        }

    }

//    private val client = HttpClient {
//        install(WebSockets)
//    }
//
//    private var session: WebSocketSession? = null
//
//    override suspend fun connect(receiveBlock: (WsMessage) -> Unit) {
//        try {
//            client.webSocket() {
//                while (true) {
//                    session = this
//                    val text = incoming.receive() as? Frame.Text ?: continue
//                    val message = Json.decodeFromString<WsMessage>(text.readText())
//                    receiveBlock(message)
//                }
//            }
//        } catch (e: Exception) {
//            println("Error: $e")
//        }
//    }
//
//    override suspend fun sendNewMessage(message: String) {
//        try {
//            session?.send(message)
//        } catch(e: Exception) {
//            println("Error: $e")
//        }
//    }
//
//    override suspend fun closeSession() {
//        session?.close()
//    }
}

interface PlatformListener {
    fun onOpen()
    fun onFailure(error: Throwable)
    fun onMessage(string: String)
    fun onClose()
}