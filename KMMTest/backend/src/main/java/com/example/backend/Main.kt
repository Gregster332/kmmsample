package com.example.backend

import com.example.backend.Authorization.configureAuth
import com.example.backend.Database.Chats.UsersController
import com.example.backend.Websockets.configureWebSocket
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Database

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    log.info("🔴Server started")
    Database.connect(
        "jdbc:postgresql://localhost:5432/localbd",
        driver = "org.postgresql.Driver",
        user = "postgres",
        password = "greg1"
    )
    //DatabaseServiceImpl.init()
    format()
    configureRouting()
    configureAuth()
    configureWebSocket()
}

fun Application.format() {
    install(ContentNegotiation) {
        json()
    }
}

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        databaseRouting()
    }
}

fun Route.databaseRouting() {
    route("/chats") {
        post("/create") {
            var decoded: NewChatRequestModel? = null
            try {
                decoded = call.receive()
            } catch(e: Exception) {
                println(e)
            }

            if (decoded is NewChatRequestModel) {
                //DB.createNewChat()
//                ChatsManager.createNewChat(
//                    decoded.chatId,
//                    decoded.name,
//                    decoded.creatorId
//                )
                call.respond(HttpStatusCode.OK, "Hello")
            }
            call.respond(HttpStatusCode.BadRequest, "OOOOps1")
        }

        get("/getAll") {
            val controller = UsersController(call)
            controller.registerNewUser("1")
            call.respond(HttpStatusCode.OK, "")
        }
    }
}

@Serializable
data class NewChatRequestModel(
    @SerialName("chat_id") val chatId: Int,
    val name: String,
    @SerialName("creator_id") val creatorId: String
)

@Serializable
data class Chat(
    val name: String,
    val participants: List<UserUnit>
)

@Serializable
data class UserUnit(
    val name: String,
    val age: Int
)