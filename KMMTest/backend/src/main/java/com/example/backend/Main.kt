package com.example.backend

import com.example.backend.Authorization.configureAuth
import com.example.backend.Database.DAOService
import com.example.backend.Database.DatabaseService
import com.example.backend.Websockets.configureWebSocket
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.util.getOrFail

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    DatabaseService.init()
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
    }
}

fun Route.userRouting() {
    val db = DAOService()

    route("/users") {
        get {
            val users = db.getAllUsers()
            call.respond(users)
        }

        post("/createNewUser") {
            println("======init new ======================")
            //db.deleteAll()
            val name = call.parameters.getOrFail<String>("name")
            println(name)
            val surname = call.parameters.getOrFail<String>("surname")
            db.appendNewUser(name, surname)
            call.respond(Unit)
        }
    }
}