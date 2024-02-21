package com.example.backend.authorization

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.backend.UserBaseInfo
import com.example.backend.db.dao.UsersDao
import io.ktor.server.auth.jwt.JWTCredential
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.util.logging.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.UUID

class JwtService(
    private val logger: Logger,
    private val userRepo: UsersDao,
) {
    private val secret = "secret"
    private val audience = "audience"

    val verifier =
        JWT
            .require(Algorithm.HMAC256(secret))
            .withAudience(audience)
            .build()

    fun createAccessToken(userInfo: UserBaseInfo) = createToken(userInfo, 600000)
    fun createRefreshToken(userInfo: UserBaseInfo) = createToken(userInfo, 1200000)

    suspend fun validate(credentials: JWTCredential): JWTPrincipal? {
        val id: String? = credentials.payload.getClaim("id").asString()
        val userInfo: UserBaseInfo? =
            id?.let {
                withContext(Dispatchers.IO) {
                    userRepo.getUserBy(UUID.fromString(it))?.mapRequestModel()
                }
            }

        logger.info(
            "find user with data $userInfo",
        )

        return userInfo?.let {
            if (isAudienceMatches(credentials)) {
                logger.info(
                    "audience mathched for user with nickname !",
                )

                JWTPrincipal(credentials.payload)
            } else {
                logger.info(
                    "audience not mathched for user with nickname !",
                )
                null
            }
        }
    }

    fun matchAudience(a: String): Boolean = this.audience == a

    fun getId(cred: JWTPrincipal): String = cred.payload.getClaim("id").asString()

    private fun isAudienceMatches(credentials: JWTCredential): Boolean = credentials.payload.audience.contains(audience)

    private fun createToken(userInfo: UserBaseInfo, expireIn: Int): String = JWT.create()
            .withAudience(audience)
            .withClaim("id", userInfo.id.toString())
            .withClaim("nickname", userInfo.nickname)
            .withClaim("email", userInfo.email)
            .withExpiresAt(Date(System.currentTimeMillis() + expireIn))
            .sign(Algorithm.HMAC256(secret))
}
