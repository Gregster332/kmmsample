package com.example.backend.websockets

import com.example.backend.db.dao.ChatsDao
import com.example.backend.db.dao.MessagesDao
import com.example.backend.db.entities.MessageMap
import com.example.backend.models.MessageUnit
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.DefaultWebSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.async
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Duration
import java.util.HashSet
import java.util.concurrent.atomic.AtomicInteger

fun Application.configureWebSocket(chatsDao: ChatsDao, messagesDao: MessagesDao) {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(120000)
        maxFrameSize = Long.MAX_VALUE
        masking = false
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
    }

    routing {
        val connections = HashSet<Connection>()

        webSocket("/chat") {
            val connection = Connection(this)
            connections += connection

            try {
                for (frame in incoming) {
                    frame as? Frame.Text ?: continue
                    val message = Json.decodeFromString<MessageUnit>(frame.readText())

                    val l = async {
                        newSuspendedTransaction {
                            chatsDao.getBy(message.chatId)?.let {
                                println(it)

                                val newMessage = messagesDao.create(
                                    MessageMap(message.message, message.senderId)
                                )
                                var newMessages = it.messages.toMutableList()
                                newMessages.add(newMessage)
                                transaction {
                                    it.messages = SizedCollection(newMessages)
                                }
                            }
                        }
                    }

                    l.await()

                    connections.forEach { connection ->
                        if (connection.session.isActive) {
                            connection.session.send(content = frame.readText())
                        }
                    }
                }
            } catch (e: Exception) {
                println(e)
            } finally {
                println("Finally")
                connections -= connection
            }
        }
    }
}

class Connection(val session: DefaultWebSocketSession) {
    companion object {
        val lastId = AtomicInteger(0)
    }
    val name = "user${lastId.getAndIncrement()}"
}