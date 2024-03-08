package com.example.backend.authorization

import com.auth0.jwt.interfaces.DecodedJWT
import com.example.backend.authorization.Models.TokensResponse
import com.example.backend.models.LoginState
import com.example.backend.UserBaseInfo
import com.example.backend.db.dao.UsersAndTokensDao
import com.example.backend.db.dao.UsersDao
import com.example.backend.db.entities.UserEntity
import com.example.backend.db.tables.UsersAndTokens
import com.example.backend.models.UsersAndTokensDataModel
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.response.respond
import io.ktor.util.logging.Logger
import kotlinx.serialization.Serializable
import org.postgresql.util.PSQLException
import java.util.UUID

@Serializable
data class UserLogInInfo(
    val userInfo: UserBaseInfo? = null,
    val loginState: LoginState,
)

fun Application.configureAuth(jwtService: JwtService) {
    install(Authentication) {
        jwt("main") {
            verifier(jwtService.verifier)

            validate {
                println("validate!!!")
                jwtService.validate(it)
            }

            challenge { _, _ ->
                println("Challenge!!!")
                call.request.headers["Authorization"]?.let {
                    if (it.isNotEmpty()) {
                        call.respond(
                            HttpStatusCode.OK,
                            UserLogInInfo(loginState = LoginState(isAccessTokenExpired = true, isAuthorized = false)),
                        )
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
    private val userRepo: UsersDao
) {
    suspend fun saveNewUser(userInfo: UserBaseInfo): UserBaseInfo? {
        logger.info(
            "start finding user data with $userInfo",
        )

        var foundUser: UserBaseInfo? = try {
            userRepo.getUserBy(UUID.fromString(userInfo.id.toString()))?.mapRequestModel()
        } catch (e: PSQLException) {
            println("Erroe: ${e.message}")
            null
        }

        logger.info(
            "Found user: $foundUser",
        )

        return if (foundUser == null) {
            logger.info(
                "saving new user: $userInfo",
            )

            return userRepo.createUser(userInfo).let {
                if (it is UserEntity) {
                    logger.info(
                        "new user saved: $it",
                    )

                    it.mapRequestModel()
                } else {
                    null
                }
            }
        } else {
            null
        }
    }

    suspend fun auth(userInfo: UserBaseInfo): TokensResponse? {
        logger.info(
            "Auth process started for user with nickname ${userInfo.nickname} and id ${userInfo.id}",
        )

        val foundUser: UserBaseInfo? = userRepo.getUserBy(UUID.fromString(userInfo.id.toString()))?.mapRequestModel()

        logger.info(
            "Found user with nickname ${userInfo.nickname} and password ${userInfo.password}",
        )

        return if (
            foundUser != null
            && foundUser.nickname == userInfo.nickname
            && foundUser.email == userInfo.email
            && foundUser.password == userInfo.password
            ) {
            val accessToken = jwtService.createAccessToken(userInfo)
            val refreshToken = jwtService.createRefreshToken(userInfo)

            logger.info(
                "create aToken $accessToken and refToken $refreshToken",
            )

            UsersAndTokensDao.createNewPair(
                UsersAndTokensDataModel(userInfo.id, accessToken, refreshToken)
            )

            TokensResponse(
                accessToken = accessToken,
                refreshToken = refreshToken,
                userId = userInfo.id
            )
        } else {
            logger.info(
                "It seems to be that passwords mismatch here: userInfo password ${userInfo.email} and password for user form db ${foundUser?.email}",
            )
            null
        }
    }

    suspend fun refresh(token: String): String? {
        logger.info(
            "Refresh process started with $token",
        )

        val decodedRefreshToken = verifyRefreshToken(token)
        val persistedId = UsersAndTokensDao.getUserIdByRefreshToken(token)

        logger.info(
            "Refresh info: decodedRefreshToken = $decodedRefreshToken and $persistedId",
        )

        return if (decodedRefreshToken != null && persistedId != null) {
            val foundUser = userRepo.getUserBy(persistedId)?.mapRequestModel()
            val nicknameFormRefreshToken: String? = decodedRefreshToken.getClaim("nickname").asString()

            logger.info(
                "Refresh info: foundUser = $foundUser and nicknameFormRefreshToken $nicknameFormRefreshToken",
            )

            if (foundUser != null && nicknameFormRefreshToken == foundUser.nickname) {
                jwtService.createAccessToken(
                    UserBaseInfo(id = foundUser.id, nickname = foundUser.nickname, email = foundUser.email),
                )
            } else {
                null
            }
        } else {
            if (persistedId != null) {
                UsersAndTokensDao.nullifyTokens(persistedId)
            }
            null
        }
    }

    suspend fun getIdFrom(credentials: JWTPrincipal) = UUID.fromString(jwtService.getId(credentials))

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
