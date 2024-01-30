package com.example.corenetwork.helpers

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.URLBuilder
import io.ktor.http.encodedPath
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.serializer

internal object HttpClientProvider {
    fun get() = HttpClient {
        expectSuccess = true
        install(ContentNegotiation) {
            json()

        }

        defaultRequest {
            //header(HttpHeaders.ContentType, ContentType.Application.Json)
            url.takeFrom(
                URLBuilder()
                    .takeFrom(Endpoints.localhost)
                    .apply { encodedPath += url.encodedPath }
            )
        }
    }

    private object Endpoints {
        const val localhost = "http://localhost.proxyman.io:8080"
    }
}