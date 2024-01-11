package com.example.mykmmtest.Services

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class PostLoader() {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }

    suspend fun fetchAllPosts(chat: Chat) = withContext(Dispatchers.IO) {
        client.request("http://localhost.proxyman.io:8080/chats/create") {
            method = HttpMethod.Post
            contentType(ContentType.Application.Json)
            setBody(chat)
        }.bodyAsText()

        //val decoded = Json.decodeFromString<Chat>(response).toList()
    }

    suspend fun getAllUserChats(): List<ChatUnit> = withContext(Dispatchers.IO) {
        val response = client.request("http://localhost.proxyman.io:8080/chats/getAll") {
            method = HttpMethod.Get
            contentType(ContentType.Application.Json)
        }.bodyAsText()
        return@withContext Json{ ignoreUnknownKeys = true }.decodeFromString<List<ChatUnit>>(response)
    }
}


@Serializable
data class Chat(
    @SerialName("chat_id") val chatId: Int,
    val name: String,
    @SerialName("creator_id") val creatorId: String
)

@Serializable
data class ChatUnit(
    val name: String,
    val participants: List<User>
)

@Serializable
data class User(val name: String, val age: Int)

sealed interface Result<out T> {
    data class Success<out R>(val data: R): Result<R>
    data class Failure(val throwable: Throwable): Result<Nothing>
}
