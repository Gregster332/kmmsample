package com.example.backend.Websockets

import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.DefaultWebSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.serialization.json.Json
import java.time.Duration
import java.util.Collections
import java.util.concurrent.atomic.AtomicInteger

fun Application.configureWebSocket() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(120)
        maxFrameSize = Long.MAX_VALUE
        masking = false
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
    }


    routing {
        val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
        //authenticate("jwt-main") {
            webSocket("/chat") {
                connections += Connection(this)
                println("size ${connections.size}")
                try {
                    for (frame in incoming) {
                        frame as? Frame.Text ?: continue
                        println("readtext ${frame.readText()}")
                        connections.forEach {
                            it.session.send(frame.readText())
                        }
                    }
                } catch (e: Exception) {
                    println(e)
                }
            }
        }
    //}
}

class Connection(val session: DefaultWebSocketSession) {
    companion object {
        val lastId = AtomicInteger(0)
    }
    val name = "user${lastId.getAndIncrement()}"
}