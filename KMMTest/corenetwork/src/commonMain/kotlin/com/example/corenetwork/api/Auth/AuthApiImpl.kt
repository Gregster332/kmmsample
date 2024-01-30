package com.example.corenetwork.api.Auth

//import com.example.corenetwork.api.SecurePersistant.SettingsKeys
import com.example.corenetwork.api.SecurePersistant.SettingsPersistent
import com.example.corenetwork.model.Auth.AuthState
import com.example.corenetwork.model.Auth.SignUpRequestEntity
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.Identity.decode
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Serializable
public data class UserBaseInfo(
    val id: String,
    val nickname: String,
    val email: String,
    val password: String,
    val photoUrl: String? = null,
    val bio: String? = null
)

class AuthApiImpl(
    private val client: HttpClient,
    private val settings: SettingsPersistent
): AuthApi {
    override suspend fun generateToken(entity: SignUpRequestEntity) = withContext(Dispatchers.IO) {
        val url = "http://localhost.proxyman.io:8080/signUp"
        try {
            val response = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(entity)
            }.body<TokensResponse>()

            if (response.accessToken.isEmpty() || response.refreshToken.isEmpty()) throw CancellationException("Empty tokens")

            settings.add("AUTH_TOKEN", response.accessToken)
            settings.add("REFRESH_TOKEN", response.refreshToken)
        } catch(e: Exception) {
            throw e
        }
    }

    override suspend fun trySignInWithToken(): AuthState = withContext(Dispatchers.IO) {
        try {
            when (val token = settings.getString("AUTH_TOKEN")) {
                is String -> {
                    val response = client.get("http://localhost.proxyman.io:8080/logIn") {
                        headers.append("Authorization", "Bearer ${token!!}")
                    }

                    val decoded = Json.decodeFromString(AuthState.serializer(), response.bodyAsText())
                    return@withContext decoded
                }
                else -> { throw Exception("token invalid") }
            }
        } catch (e: Exception) {
            throw Exception(e.message)
        }
    }

    override suspend fun refreshIdToken() {
        val token = settings.getString("REFRESH_TOKEN").let {
            if (it is String) {
                it
            } else {
                throw CancellationException("")
            }
        }

        try {
            val response = client.post("http://localhost.proxyman.io:8080/refresh") {
                contentType(ContentType.Application.Json)
                setBody(RefreshTokenHandler(token))
            }.body<RefreshTokenResult>()

            settings.add("AUTH_TOKEN", response.accessToken)
        } catch (e: Exception) {
            throw CancellationException(e.message)
        }
    }
}

@Serializable
data class RefreshTokenHandler(
    val token: String
)

@Serializable
data class RefreshTokenResult (
    val accessToken: String
)

@Serializable
data class TokensResponse (
    val accessToken: String,
    val refreshToken: String
)
