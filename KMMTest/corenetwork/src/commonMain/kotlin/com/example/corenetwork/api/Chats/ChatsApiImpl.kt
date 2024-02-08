package com.example.corenetwork.api.Chats

import com.example.corenetwork.api.Auth.LocalCache
import com.example.corenetwork.api.SecurePersistant.SettingsPersistent
import com.example.corenetwork.model.Chats.Chat
import com.example.corenetwork.model.Chats.ChatUnit
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.path
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class ChatsApiImpl(
    private val client: HttpClient,
    private val settings: SettingsPersistent
): ChatsApi, KoinComponent {

    private val localCache: LocalCache by inject()

    override suspend fun createNewChat(entity: CreateFaceToFaceChatRequest): ChatUnit? = when (
        val token = settings.getString("AUTH_TOKEN")
    ) {
        is String -> {
            try {
                val response = client
                    .post("http://localhost.proxyman.io:8080/chats/create") {
                        bearerAuth(token)
                        contentType(ContentType.Application.Json)
                        setBody(entity)
                    }
                    .bodyAsText()
                Json { ignoreUnknownKeys = true }.decodeFromString<ChatUnit>(response)
            } catch (e: Exception) {
                println(e)
                null
            }
        }
        else -> null
    }

    override suspend fun getAllChats(): List<ChatUnit> {
        val currentUserId = localCache.getCurrentUser()?.id ?: ""
        if (currentUserId.isEmpty()) return emptyList()

        return when(val token = settings.getString("AUTH_TOKEN")) {
            is String -> {
                try {
                    val response = client
                        .get("http://localhost.proxyman.io:8080/chats") {
                            bearerAuth(token)
                            accept(ContentType.Application.Json)
                            parameter("userId", currentUserId)
                        }
                        .bodyAsText()
                    Json { ignoreUnknownKeys = true }.decodeFromString<List<ChatUnit>>(response)
                } catch(e: Exception) {
                    emptyList()
                }
            }
            else -> emptyList()
        }
    }
}

@Serializable
data class CreateFaceToFaceChatRequest(
    val name: String,
    val ownerId: String,
    val opponent: String
)
//
//@Serializable
//data class Chat(
//    @SerialName("id")
//    val id: String,
//
//    @SerialName("name")
//    val name: String,
//
//    @SerialName("chat_type")
//    val chatType: Long
//)
