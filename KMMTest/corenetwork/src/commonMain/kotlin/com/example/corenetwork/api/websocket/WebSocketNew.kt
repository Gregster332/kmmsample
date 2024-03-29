package com.example.corenetwork.api.websocket

import com.example.corenetwork.api.chats.MessageUnit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.http.HttpMethod
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readReason
import io.ktor.websocket.readText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

interface WebSocketNew {
    suspend fun connect(block: suspend (MessageUnit?, Throwable?) -> Unit)
    suspend fun sendMessage(message: String)
    suspend fun close()
}

class WebSocketCloseException(message: String): Exception(message)

internal class WebSocketNewImpl : WebSocketNew {
    private val client =
        HttpClient {
            install(WebSockets) {
                pingInterval = 15000
                maxFrameSize = Long.MAX_VALUE
            }
        }

    private var session: WebSocketSession? = null

    override suspend fun connect(block: suspend (MessageUnit?, Throwable?) -> Unit) = try {
        session = runBlocking {
            client
                .webSocketSession(
                    method = HttpMethod.Get,
                    host = "0.0.0.0",
                    8080,
                    path = "/chat",
                )
        }

        client
            .webSocket(
                method = HttpMethod.Get,
                host = "0.0.0.0",
                8080,
                path = "/chat",
            ) {
                incoming.receiveAsFlow().collect {
                    when (it) {
                        is Frame.Text -> {
                            val messageUnit = Json.decodeFromString<MessageUnit>(it.readText())
                            block.invoke(messageUnit, null)
                        }
                        is Frame.Close -> {
                            throw WebSocketCloseException(it.readReason()?.message ?: "")
                        }
                        else -> {}
                    }
                }
            }
    } catch (e: Exception) {
        println(e)
        block.invoke(null, e)
    }

    override suspend fun sendMessage(message: String): Unit = withContext(Dispatchers.IO) {
        try {
            session?.outgoing?.send(Frame.Text(message))
        } catch (e: Exception) {
            println(e)
        }
    }

    override suspend fun close() {
        session?.close(CloseReason(CloseReason.Codes.GOING_AWAY, "User goes away"))
        session = null
    }
}
