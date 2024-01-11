package com.example.mykmmtest.Services.AuthService

import com.example.mykmmtest.Services.AuthService.Models.SendAuthRequestEntity
import com.example.mykmmtest.Services.SecurePersistant.SecurePersistant
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.Identity.decode
import io.ktor.util.InternalAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

sealed class ExceptionsError {
    data class BadRequestError(override val message: String): Exception(message)
}

interface AuthService {
    suspend fun sendAuthRequest(entity: SendAuthRequestEntity)

    @Throws(Exception::class)
    suspend fun trySignInWithToken(): AuthState
}

class AuthServiceImpl(private val setting: SecurePersistant): AuthService, KoinComponent {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }

    //10.0.2.2
    override suspend fun sendAuthRequest(entity: SendAuthRequestEntity) = withContext(Dispatchers.IO) {
        val url = "http://localhost.proxyman.io:8080/login"
        try {
            val response = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(entity)
            }.bodyAsText()

            val decoded = Json.decodeFromString<Token>(response)
            setting.set(decoded.token, "auth_token")
        } catch(e: Exception) {
            throw e
        }
    }

    override suspend fun trySignInWithToken(): AuthState = withContext(Dispatchers.IO) {
        try {
            when (val token = setting.getValue("auth_token")) {
                is String -> {
                    val response = client.request("http://localhost.proxyman.io:8080/isAuthorizedUser") {
                        method = HttpMethod.Get
                        headers.append("Authorization", "Bearer ${token!!}")
                    }.bodyAsText()
                    val decoded = Json.decodeFromString<AuthState>(response)
                    return@withContext decoded
                }
                else -> {
                    throw ExceptionsError.BadRequestError("token invalid")
                }
            }
        } catch (e: Exception) {
            throw ExceptionsError.BadRequestError(e.message ?: "")
        }
    }
}

@Serializable
data class Token(
    val token: String
)

@Serializable
data class AuthState(
    val state: AuthStateProp
)

@Serializable
data class AuthStateProp(
    val isAuthorized: Boolean
)