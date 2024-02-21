package com.example.corenetwork.api.auth

import com.example.core.Services.KeyVaultStorage
import com.example.core.Services.SettingsKeys
import com.example.core.Services.SettingsPersistent
import com.example.core.Services.SettingsValue
import com.example.core.Services.value
import com.example.corenetwork.model.auth.AuthState
import com.example.corenetwork.model.auth.SignUpRequestEntity
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Serializable
data class UserBaseInfo(
    val id: String,
    val nickname: String,
    val email: String,
    val password: String,
    val photoUrl: String? = null,
    val bio: String? = null,
)

@Serializable
data class UserLogInInfo(
    val userInfo: UserBaseInfo? = null,
    val loginState: AuthState = AuthState(false, false),
)

@Serializable
data class DBUser(
    val id: String,
    val nickname: String,
    val email: String,
    val photoUrl: String? = null,
    val bio: String? = null,
    val current: Boolean = false,
)

fun UserBaseInfo.mapToRequestModel(isCurrent: Boolean = false) = DBUser(
    id = id,
    nickname = nickname,
    email = email,
    photoUrl = photoUrl,
    bio = bio,
    current = isCurrent,
)

interface LocalCache {
    fun getAllUsers(): List<DBUser>

    @Throws(NoSuchElementException::class)
    fun getUserBy(id: String): DBUser?
    fun deleteAllUsers()
    fun saveNewUser(user: DBUser)
    fun getCurrentUser(): DBUser?
}

internal class AuthApiImpl(
    private val client: HttpClient,
    private val kVault: KeyVaultStorage,
    private val settingsPersistent: SettingsPersistent,
) : AuthApi, KoinComponent {
    private val localCache: LocalCache by inject()

    override suspend fun generateToken(entity: SignUpRequestEntity): Boolean = withContext(Dispatchers.IO) {
        if (settingsPersistent.get(SettingsValue.BoolValue(SettingsKeys.skipAuthKey)).value() as Boolean) {
            return@withContext true
        }
        val url = "http://localhost.proxyman.io:8080/signUp"
        try {
            val response = client.post(url) {
                    contentType(ContentType.Application.Json)
                    setBody(entity)
                }.body<TokensResponse>()

            if (response.accessToken.isEmpty() || response.refreshToken.isEmpty()) {
                return@withContext false
            }

            kVault.setAuthToken(response.accessToken)
            kVault.setRefreshToken(response.refreshToken)
            println(kVault.getAuthToken())
            println(kVault.getRefreshToken())
            true
        } catch (e: Exception) {
            println(e)
            false
        }
    }

    override suspend fun trySignInWithToken(): UserLogInInfo = withContext(Dispatchers.IO) {
        try {
            when (val token = kVault.getAuthToken()) {
                is String -> {
                    val response = client.get("http://localhost.proxyman.io:8080/logIn") {
                            headers.append("Authorization", "Bearer ${token}")
                        }

                    val decoded =
                        Json.decodeFromString(
                            UserLogInInfo.serializer(),
                            response.bodyAsText(),
                        )

                    decoded.userInfo?.let {
                        localCache.saveNewUser(it.mapToRequestModel(true))
                    }

                    return@withContext decoded
                }
                else -> {
                    throw Exception("token invalid")
                }
            }
        } catch (e: Exception) {
            throw Exception(e.message)
        }
    }

    override suspend fun refreshIdToken() {
        val token = kVault.getRefreshToken() ?: ""

        try {
            val response =
                client.post("http://localhost.proxyman.io:8080/refresh") {
                    contentType(ContentType.Application.Json)
                    setBody(RefreshTokenHandler(token))
                }.body<RefreshTokenResult>()

            kVault.setAuthToken(response.accessToken)
        } catch (e: Exception) {
            throw CancellationException(e.message)
        }
    }
}

@Serializable
data class RefreshTokenHandler(
    val token: String,
)

@Serializable
data class RefreshTokenResult(
    val accessToken: String,
)

@Serializable
data class TokensResponse(
    val accessToken: String,
    val refreshToken: String,
)
