package com.example.backend

import com.example.backend.Authorization.JwtService
import com.example.backend.Authorization.Tokens.RefreshTokenRepository
import com.example.backend.Authorization.UserAuthenticator
import com.example.backend.Authorization.configureAuth
import com.example.backend.DB.ChatEntity
import com.example.backend.DB.Chats

import com.example.backend.DB.ChatsDao
import com.example.backend.DB.DatabaseSingleton
import com.example.backend.DB.MessageEntity
import com.example.backend.DB.MessagesDao
import com.example.backend.DB.UserModel
import com.example.backend.DB.UsersDao
import com.example.backend.DB.UsserEntity
import com.example.backend.DB.Ussers
import com.example.backend.Models.LoginState
import com.example.backend.Websockets.configureWebSocket
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.serialization.Contextual
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.vendors.ForUpdateOption
import java.util.Random
import java.util.UUID

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    log.info("Server started")

    DatabaseSingleton.init()
    val userRepo = UsersDao()
    val chats = ChatsDao()
    val messages = MessagesDao()
    val refreshTokenRepository = RefreshTokenRepository()
    val jwtService = JwtService(log, userRepo)
    val userAuthenticator = UserAuthenticator(log, jwtService, userRepo, refreshTokenRepository)

    format()
    configureAuth(jwtService)
    configureWebSocket()
    configureRouting(userAuthenticator, userRepo, chats, messages)
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
    messagesDao: MessagesDao
) {
    routing {
        //val users = Collections.synchronizedSet<UserBaseInfo>(LinkedHashSet())

        post("/signUp") {
            val userInfo = call.receive<UserBaseInfo>()

            userService.saveNewUser(
                userInfo
            ) ?: return@post call.respond(HttpStatusCode.BadRequest)

            val tokenResponse = userService.auth(userInfo)

            tokenResponse?.let {
                call.respond(tokenResponse)
            } ?: call.respond(HttpStatusCode.Unauthorized)
        }

        post("/refresh") {
            val request = call.receive<TokenHandler>()
            val newAccessToken = userService.refresh(request.token)

            newAccessToken?.let {
                call.respond(
                    AccessTokenHandler(it)
                )
            } ?: call.respond(
                HttpStatusCode.Unauthorized
            )
        }

        get("/testNewUser") {
            val userModel = UserModel(nickname = "Greg", email = "dsds", password = "dsd")
            println(userModel)
            val newUser = userRepo.create(userModel)?.let {
                println(it)
                call.respond(HttpStatusCode.OK, it)
            }
        }

        post("/testNewChat") {
            println("calll ${call.parameters}")
            val uuid = UUID.fromString(call.parameters["id"])
            userRepo.getBy(uuid)?.let {
                val chat = NewChatRequestModel("test1", it.id)
                chatsDao.create(chat)?.let {
                    println(it)
                    call.respond(HttpStatusCode.OK, it)
                }
            }
        }

        get("/addM") {
            DatabaseSingleton.dbQuery {
                ChatEntity.findById(UUID.fromString("5540150b-d7b1-4027-8a90-88218c70a7e2"))?.let {
                    val newMessage = MessageEntity.new {
                        message_text = "New test${Random().nextInt()} message"
                    }

                    val user = UsserEntity.findById(UUID.fromString("334728d2-56ba-4740-9bfa-087593d6aa9c"))
                    when(user) {
                        is UsserEntity -> {
                            var partForUpdate = it.participants.toMutableList()
                            partForUpdate.add(user)
                            val currentMessages = it.messages.toMutableList()
                            currentMessages.add(newMessage)
                            it.messages = SizedCollection(currentMessages)
                            println(partForUpdate)
                            it.participants = SizedCollection(partForUpdate)
                        }
                        else -> call.respond(HttpStatusCode.BadRequest)
                    }

                    //call.respond(HttpStatusCode.OK)
                }
//                ChatEntity.findById(UUID.fromString("c194a6be-6d2e-42b0-8314-fffe93b6e6bc"))?.let {
//                    val messages = it.messages.map { it.toMessage() }
//                    val par = it.participants
//                    println(par.map { it.mapUsers() })
//                    call.respond(HttpStatusCode.OK)
//                }
//                UsserEntity.findById(UUID.fromString("5e95370c-9994-4686-acd6-530f6a8215e5"))?.let {
//                    val users = it.ch
//                    println(users)
//                    call.respond(HttpStatusCode.OK)
//                }
            }
        }

        authenticate("main") {
            get("/logIn") {
                call.respond(LoginState(true))
            }

            get("/users") {
                //val users = userRepo.getAllUsers()
                //println("Get users: $users")
                //call.respond(HttpStatusCode.OK, users)
            }
        }
    }
}

@Serializable
data class NewChatRequestModel(
    val name: String,
    @Serializable(with = UUIDSerializer::class)
    val ownerId: UUID
)

object UUIDSerializer: KSerializer<UUID> {
    override val descriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): UUID {
        return UUID.fromString(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: UUID) {
        return encoder.encodeString(value.toString())
    }
}


@Serializable
data class UserBaseInfo(
    val id: String = UUID.randomUUID().toString(),
    val nickname: String = "",
    val email: String = "",
    val password: String = "",
    val bio: String? = null,
    val photoUrl: String? = null
)

//@Serializable

@Serializable
data class TokenHandler(
    val token: String = ""
)

@Serializable
data class AccessTokenHandler(
    val accessToken: String
)


@Serializable
data class RefreshTokenResult (
    @SerialName("expires_in")
    val expiresIn: String,

    @SerialName("token_type")
    val tokenType: String,

    @SerialName("refresh_token")
    val refreshToken: String,

    @SerialName("id_token")
    val idToken: String,

    @SerialName("user_id")
    val userID: String,

    @SerialName("project_id")
    val projectID: String
)