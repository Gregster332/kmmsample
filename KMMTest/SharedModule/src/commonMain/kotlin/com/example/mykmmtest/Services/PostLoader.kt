package com.example.mykmmtest.Services

import com.example.mykmmtest.Models.Post
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

class PostLoader() {
    private var client: HttpClient = HttpClient()

    suspend fun fetchAllPosts(): Result<List<Post>> {
        delay(2500L)
        val response =
            client.get("https://jsonplaceholder.typicode.com/posts").bodyAsText()
        val decoded = Json.decodeFromString<Array<Post>>(response).toList()
        return Result.Success(decoded)
    }
}

sealed interface Result<out T> {
    data class Success<out R>(val data: R): Result<R>
    data class Failure(val throwable: Throwable): Result<Nothing>
}
