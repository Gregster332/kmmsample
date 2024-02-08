package com.example.backend.Authorization

import com.auth0.jwt.interfaces.DecodedJWT
import com.example.backend.Authorization.Models.TokensResponse
import com.example.backend.Authorization.Tokens.RefreshTokenRepository
import com.example.backend.DB.UserModel
import com.example.backend.DB.UsersDao
import com.example.backend.DB.UsserEntity
import com.example.backend.DB.toUserModel
import com.example.backend.DB.toWebModel
import com.example.backend.Models.LoginState
import com.example.backend.UserBaseInfo
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
    val userInfo: UserModel? = null,
    val loginState: LoginState = LoginState()
)

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
                        call.respond(
                            HttpStatusCode.OK,
                            UserLogInInfo(loginState = LoginState(isAccessTokenExpired = true))
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
    private val userRepo: UsersDao,
    private val tokensRepo: RefreshTokenRepository
) {
    suspend fun saveNewUser(userInfo: UserBaseInfo): UserBaseInfo? {
        logger.info(
            "start finding user data with $userInfo"
        )

        var foundUser: UserModel?
        try {
            foundUser = userRepo.getUserBy(UUID.fromString(userInfo.id))?.mapUsers()
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

            return userRepo.createUser(userInfo.toUserModel()).let {
                if (it is UsserEntity) {
                    logger.info(
                        "new user saved: $it"
                    )

                    it.mapUsers().toWebModel()
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
            "Auth process started for user with nickname ${userInfo.nickname} and id ${userInfo.id}"
        )

        val foundUser: UserModel? = userRepo.getUserBy(UUID.fromString(userInfo.id))?.mapUsers()

        logger.info(
            "Found user with nickname ${userInfo.nickname} and password ${userInfo.password}"
        )

        return if (foundUser != null && foundUser.nickname == userInfo.nickname) {
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
            val foundUser = userRepo.getUserBy(UUID.fromString(persistedId))?.mapUsers()
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