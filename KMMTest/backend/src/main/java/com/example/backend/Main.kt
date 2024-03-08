package com.example.backend

import com.example.backend.authorization.JwtService
import com.example.backend.authorization.UserAuthenticator
import com.example.backend.authorization.UserLogInInfo
import com.example.backend.authorization.configureAuth
import com.example.backend.db.DatabaseSingleton
import com.example.backend.db.dao.ChatsDao
import com.example.backend.db.dao.MessagesDao
import com.example.backend.db.dao.UsersAndTokensDao
import com.example.backend.db.dao.UsersDao
import com.example.backend.models.CreateFaceToFaceChatRequest
import com.example.backend.models.LoginState
import com.example.backend.models.MessageUnit
import com.example.backend.models.mapTo
import com.example.backend.utils.serializers.UUIDSerializer
import com.example.backend.websockets.configureWebSocket
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.header
import io.ktor.server.request.receive
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.TimeZone
import java.util.UUID

fun main() {
    TimeZone.setDefault(TimeZone.getTimeZone("GMT+3"))
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    log.info("Server started")
    DatabaseSingleton.init()
    val userRepo = UsersDao()
    val chats = ChatsDao()
    val messages = MessagesDao()
    val jwtService = JwtService(log, userRepo)
    val userAuthenticator = UserAuthenticator(log, jwtService, userRepo)

    format()
    configureAuth(jwtService)
    configureWebSocket(userRepo, chats, messages)
    configureRouting(userAuthenticator, userRepo, chats, messages)
    chatsRouting(userRepo, chats, messages)
}

fun Application.format() {
    install(ContentNegotiation) {
        json()
    }
}

fun Application.configureRouting(
    userService: UserAuthenticator,
    userRepo: UsersDao,
    chatsDao: ChatsDao,
    messagesDao: MessagesDao,
) {
    routing {
        get("test") {
            call.respond(HttpStatusCode.OK)
        }

        post("/signUp") {
            val userInfo = call.receive<UserBaseInfo>()

            val isUserExists = newSuspendedTransaction { -> Boolean
                    return@newSuspendedTransaction userRepo.getUserBy(
                        userInfo.nickname,
                    )?.firstOrNull()?.mapRequestModel().let { user -> Boolean
                            if (user != null) {
                                println(it)
                                val tokenResponse = userService.auth(user)
                                tokenResponse?.let {
                                    call.respond(tokenResponse)
                                    true
                                } ?: false
                            } else {
                                call.respond(HttpStatusCode.Unauthorized)
                                false
                            }
                        }
                }

            if (isUserExists) {
                return@post
            }

            val user = userService.saveNewUser(
                    userInfo,
                ) ?: return@post call.respond(HttpStatusCode.BadRequest)
            val tokenResponse = userService.auth(user)

            tokenResponse?.let {
                call.respond(tokenResponse)
            } ?: call.respond(HttpStatusCode.Unauthorized)
        }

        post("/refresh") {
            val request = call.receive<TokenHandler>()
            val newAccessToken = userService.refresh(request.token)

            newAccessToken?.let {
                call.respond(
                    AccessTokenHandler(it),
                )
            } ?: call.respond(
                HttpStatusCode.Unauthorized, "О бля а мы и не думали что токен то просрочился",
            )
        }

        authenticate("main") {
            get("/logIn") {
                // if problems happen remove block and uncomment last line
                call.principal<JWTPrincipal>()?.let {
                    val id = userService.getIdFrom(it)
                    userRepo.getUserBy(id)?.mapRequestModel().let { model ->
                        call.respond(
                            HttpStatusCode.OK,
                            UserLogInInfo(
                                userInfo = model,
                                loginState = LoginState(isAuthorized = true, isAccessTokenExpired = false),
                            ),
                        )
                    } ?: call.respond(HttpStatusCode.BadRequest)
                } ?: call.respond(HttpStatusCode.BadRequest)
            }

            get("/logOut") {
                call.parameters["id"]?.let {
                    UsersAndTokensDao.nullifyTokens(UUID.fromString(it))
                    call.respond(
                        HttpStatusCode.OK,
                        UserLogInInfo(
                            userInfo = null,
                            loginState = LoginState(isAuthorized = false, isAccessTokenExpired = false),
                        )
                    )
                } ?: call.respond(HttpStatusCode.BadRequest)
            }

            get("/users") {
                val users = userRepo.getAllUsers()
                newSuspendedTransaction {
                    println("Get users: ${users.map { it.mapRequestModel() }}")
                    call.respond(HttpStatusCode.OK, users.map { it.mapRequestModel() })
                }
            }

            get("/users/byId") {
                call.parameters["id"]?.let {
                    userRepo.getUserBy(UUID.fromString(it))?.let { user ->
                        call.respond(HttpStatusCode.OK, user.mapRequestModel())
                    } ?: call.respond(HttpStatusCode.NotFound)
                } ?: call.respond(HttpStatusCode.BadRequest)
            }

            get("/users/bysearch") {
                call.parameters["search_text"]?.let { searchText ->
                    newSuspendedTransaction {
                        val users =
                            userRepo.getAllUsers()
                                .filter { it.nickname.contains(searchText) }
                                .map { it.mapRequestModel() }

                        call.respond(HttpStatusCode.OK, users)
                    }
                } ?: call.respond(HttpStatusCode.BadRequest)
            }
        }
    }
}

fun Application.chatsRouting(usersDao: UsersDao, chatsDao: ChatsDao, messagesDao: MessagesDao) {
    routing {
        authenticate("main") {
            get("/chats") {
                val userId: String? = call.parameters["userId"]
                if (userId == null) call.respond(HttpStatusCode.BadRequest)
                val chats = chatsDao.getChatsBy(UUID.fromString(userId))
                call.respond(HttpStatusCode.OK, chats)
            }

            post("/chats/create") {
                call.receiveNullable<CreateFaceToFaceChatRequest>()?.let {
                    val owner = usersDao.getUserBy(it.ownerId)
                    val opponent = usersDao.getUserBy(it.opponent)

                    if (owner != null && opponent != null) {
                        val newChat = chatsDao.create(it.mapTo(), listOf(owner, opponent))
                        call.respond(HttpStatusCode.OK, newChat)
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                } ?: call.respond(HttpStatusCode.BadRequest)
            }

            get("/chats/getMessages") {
                call.parameters["chatId"]?.let { chatId ->
                    newSuspendedTransaction {
                        chatsDao.getBy(UUID.fromString(chatId))?.messages?.let { messages ->
                            val respond = messages.map { it.toMessage() }
                                .map {
                                    MessageUnit(
                                        UUID.fromString(chatId),
                                        it.messageText,
                                        it.senderId,
                                        usersDao.getUserBy(it.senderId)?.nickname ?: ""
                                    )
                                }

                            call.respond(HttpStatusCode.OK, respond)
                        } ?: call.respond(HttpStatusCode.NotFound, "Messages are empty or null")
                    }
                } ?: call.respond(HttpStatusCode.NotFound, "Chat not found")
            }
        }
    }
}

@Serializable
data class NewChatRequestModel(
    val name: String,
    @Serializable(with = UUIDSerializer::class)
    val ownerId: UUID,
)

@Serializable
data class NewChatRespondModel(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val name: String,
    @Serializable(with = UUIDSerializer::class)
    val ownerId: UUID,
)

@Serializable
data class TokenHandler(
    val token: String = "",
)

@Serializable
data class AccessTokenHandler(
    val accessToken: String,
)

@Serializable
data class UserBaseInfo(
    @Serializable(UUIDSerializer::class)
    val id: UUID = UUID.randomUUID(),
    val nickname: String = "",
    val email: String = "",
    val password: String = "",
    val bio: String? = null,
    val photoUrl: String? = null,
)
