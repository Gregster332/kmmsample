package com.example.backend.Authorization

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.backend.Models.LoginState
import com.example.backend.Models.UserAUTH
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.auth.principal
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import java.util.Date

fun Application.configureAuth() {
    install(Authentication) {
        jwt("jwt-main") {
            realm = "http://0.0.0.0:8080"

            verifier(
                JWT
                    .require(Algorithm.HMAC256("secret"))
                    .withIssuer("issuer")
                    .build()
            )

            validate { credentials ->
                if (
                    credentials.payload.getClaim("nickname").asString() != "" &&
                    credentials.payload.getClaim("phone_number").asString() != ""
                ) {
                    println("Succ ${credentials.payload.getClaim("nickname")}")
                    JWTPrincipal(credentials.payload)
                } else {
                    println("fail")
                    null
                }
            }

            challenge { defScheme, realm ->
                println("dshjhsjhsjhdjshd")
            }
        }
    }

    routing {
        post("/login") {
            //call.application.log.info("🔴 Login attempt for call ${call.request}")
            var userAuthData: UserAUTH? = null

            try {
                userAuthData = call.receive()
            } catch (e: Exception) {
                println("decode data login error $e")
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Ooops! Some user data in request was missed! Double-check your request and repeat"
                )
                return@post
            }

            if (userAuthData == null) {
                println("decoded data null login")
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Ooops! User data from request is NULL! Double-check your request and repeat"
                )
            } else {
                val token = JWT.create()
                    .withIssuer("issuer")
                    .withClaim("phone_number", userAuthData?.phoneNumber)
                    .withClaim("nickname", userAuthData?.nickname)
                    .withExpiresAt(Date(System.currentTimeMillis() + 100000))
                    .sign(Algorithm.HMAC256("secret"))

                call.respond(hashMapOf("token" to token))
            }
        }

        authenticate("jwt-main") {
            get("/isAuthorizedUser") {
                var principal: JWTPrincipal? = null
                try {
                    println(call.principal<JWTPrincipal>())
                    principal = call.principal()
                } catch(e: Exception) {
                    println("decode data error isAuth $e")
                    call.respond(
                        HttpStatusCode.BadRequest,
                        "JWT token is empty for this session. Try to sign in with your credentials"
                    )
                    return@get
                }

                if (principal == null) {
                    println("decoded data null isAuth")
                    call.respond(
                        HttpStatusCode.BadRequest,
                        "JWT token is empty for this session. Try to sign in with your credentials"
                    )
                    return@get
                } else {
                    val nickname = principal?.payload?.getClaim("nickname")?.asString() ?: ""
                    val phoneNumber = principal?.payload?.getClaim("phone_number")?.asString() ?: ""
                    if (nickname.isNotEmpty() && phoneNumber.isNotEmpty()) {
                        call.respond(hashMapOf("state" to LoginState(true)))
                    } else {
                        call.respond(hashMapOf("state" to LoginState(false)))
                    }
                }
            }
        }
    }
}