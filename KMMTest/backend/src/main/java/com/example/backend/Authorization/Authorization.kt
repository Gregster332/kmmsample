package com.example.backend.Authorization

import com.auth0.jwt.interfaces.DecodedJWT
import com.example.backend.Authorization.Models.TokensResponse
import com.example.backend.Authorization.Tokens.RefreshTokenRepository
import com.example.backend.DB.UserModel
import com.example.backend.DB.UsersDao
import com.example.backend.DB.toUserModel
import com.example.backend.Models.LoginState
import com.example.backend.UserBaseInfo
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.response.respond
import io.ktor.util.logging.Logger
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.postgresql.util.PSQLException
import java.util.UUID

fun Application.configureAuth(
    jwtService: JwtService
) {
    install(Authentication) {
        jwt("main") {
            verifier(jwtService.verifier)

            validate {
                jwtService.validate(it)
            }

            challenge { _, _ ->
                call.request.headers["Authorization"]?.let {
                    if (it.isNotEmpty()) {
                       call.respond(LoginState(isAccessTokenExpired = true))
                    } else {
                        throw BadRequestException("Authorization header can not be blank!")
                    }
                } ?: throw BadRequestException("Authorization header can not be blank!")
            }
        }
    }
}

class UserAuthenticator(
    private val logger: Logger,
    private val jwtService: JwtService,
    private val userRepo: UsersDao,
    private val tokensRepo: RefreshTokenRepository
) {
    suspend fun saveNewUser(userInfo: UserBaseInfo): UserBaseInfo? {
        logger.info(
            "start finding user data with $userInfo"
        )

        //println("USERSSSSSSS: ${userRepo.allUsers()}")
        var foundUser: UserModel?
        try {
            //foundUser = userRepo.userByNickname(userInfo.nickname)
            foundUser = userRepo.getBy(UUID.fromString(userInfo.id))
        } catch (e: PSQLException) {
            println("Erroe: ${e.message}")
           foundUser = null
        }

        logger.info(
            "Found user: $foundUser"
        )

        return if (foundUser == null) {

            logger.info(
                "saving new user: $userInfo"
            )

            //userRepo.addNewUser(userInfo)
            userRepo.create(userInfo.toUserModel())
            //userRepo.insert(.toUserModel())
            println(userRepo.getAll())

            logger.info(
                "new user saved: $userInfo"
            )

            userInfo
        } else {
            null
        }
    }

    suspend fun auth(userInfo: UserBaseInfo): TokensResponse? {

        logger.info(
            "Auth process started for user with nickname ${userInfo.nickname}"
        )

        val nickname = userInfo.nickname
        //val foundUser: UserModel? = userRepo.userByNickname(nickname)
        val foundUser: UserModel? = userRepo.getBy(UUID.fromString(userInfo.id))

        logger.info(
            "Found user with nickname ${userInfo.nickname} and password ${userInfo.password}"
        )

        return if (foundUser != null && foundUser.email == userInfo.email) {
            val accessToken = jwtService.createAccessToken(userInfo)
            val refreshToken = jwtService.createRefreshToken(userInfo)

            logger.info(
                "create aToken ${accessToken} and refToken $refreshToken"
            )

            tokensRepo.save(refreshToken, userInfo.id)

            TokensResponse(
                accessToken = accessToken,
                refreshToken = refreshToken
            )
        } else {
            logger.info(
                "It seems to be that passwords mismatch here: userInfo password ${userInfo.email} and password for user form db ${foundUser?.email}"
            )
            null
        }
    }

    suspend fun refresh(token: String): String? {

        logger.info(
            "Refresh process started with ${token}"
        )

        val decodedRefreshToken = verifyRefreshToken(token)
        val persistedId = tokensRepo.findUsernameByToken(token)

        logger.info(
            "Refresh info: decodedRefreshToken = $decodedRefreshToken and $persistedId"
        )

        return if(decodedRefreshToken != null && persistedId != null) {
            //val foundUser: UserModel? = userRepo.userById(persistedId)
            val foundUser = userRepo.getBy(UUID.fromString(persistedId))
            val nicknameFormRefreshToken: String? = decodedRefreshToken.getClaim("nickname").asString()

            logger.info(
                "Refresh info: foundUser = $foundUser and nicknameFormRefreshToken $nicknameFormRefreshToken"
            )

            if (foundUser != null && nicknameFormRefreshToken == foundUser.nickname) {
                jwtService.createAccessToken(
                    UserBaseInfo(id = foundUser.id.toString(), nickname = foundUser.nickname, email = foundUser.email)
                )
            } else {
                null
            }
        } else {
            null
        }
    }

    private fun verifyRefreshToken(token: String): DecodedJWT? {
        val decodedJWT: DecodedJWT? = getDecodedJwt(token)

        return decodedJWT?.let {
            val audienceMatches = jwtService.matchAudience(it.audience.first())

            if (audienceMatches) decodedJWT else null
        }
    }

    private fun getDecodedJwt(token: String): DecodedJWT? = try {
        jwtService.verifier.verify(token)
    } catch (e: Exception) {
        null
    }
}