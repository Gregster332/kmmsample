package com.example.mykmmtest.Services

import com.example.mykmmtest.Models.Post
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.DefaultHttpRequest
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.request
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import io.ktor.http.parameters
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json



class PostLoader() {
    //private var client: HttpClient = HttpClient()
    private val client = HttpClient()

    init {

    }

    suspend fun fetchAllPosts(): Result<List<Post>> = withContext(Dispatchers.IO) {
        delay(2500L)
        val response =
            client.get("https://jsonplaceholder.typicode.com/posts").bodyAsText()
        val decoded = Json.decodeFromString<Array<Post>>(response).toList()
        return@withContext Result.Success(decoded)
    }

    suspend fun addNewUser(name: String, surname: String) = runBlocking(Dispatchers.IO) {
        val url = "http://localhost.proxyman.io:8080/users/createNewUser"
        client.request(url) {
            method = HttpMethod.Post
            parameter("name", name)
            parameter("surname", surname)
        }
    }

    suspend fun getsss() = runBlocking(Dispatchers.IO) {
        println("===============")
        //delay(10000L)
        val url = "http://localhost.proxyman.io:8080/users"
        val response = client.get(url).bodyAsText()
        val decode = Json.decodeFromString<Array<User>>(response).toList()
        println(decode)
        println("===============")
    }
}

@Serializable
data class User(val name: String, val surname: String)

sealed interface Result<out T> {
    data class Success<out R>(val data: R): Result<R>
    data class Failure(val throwable: Throwable): Result<Nothing>
}
