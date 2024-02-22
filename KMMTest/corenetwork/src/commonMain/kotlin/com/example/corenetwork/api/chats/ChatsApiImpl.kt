package com.example.corenetwork.api.chats

import com.example.core.Services.KeyVaultStorage
import com.example.core.Services.SettingsKeys
import com.example.core.Services.SettingsPersistent
import com.example.core.Services.SettingsValue
import com.example.core.Services.value
import com.example.corenetwork.api.auth.LocalCache
import com.example.corenetwork.model.chats.ChatUnit
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class ChatsApiImpl(
    private val client: HttpClient,
    private val kVault: KeyVaultStorage,
    private val settingsPersistent: SettingsPersistent
) : ChatsApi, KoinComponent {
    private val localCache: LocalCache by inject()

    override suspend fun createNewChat(entity: CreateFaceToFaceChatRequest): ChatUnit? = when (
        val token = kVault.getAuthToken()
    ) {
        is String -> {
            try {
                client.post(
                    "http://localhost.proxyman.io:8080/chats/create"
                ) {
                    bearerAuth(token)
                    contentType(ContentType.Application.Json)
                    setBody(entity)
                }
                    .body<ChatUnit>()
            } catch (e: Exception) {
                println(e)
                null
            }
        }
        else -> null
    }

    override suspend fun getAllChats(): List<ChatUnit> {
        if (settingsPersistent.get(SettingsValue.BoolValue(SettingsKeys.fakeChats))
                .value() as Boolean
        ) {
            return ChatUnit.chatsUnitMock()
        }

        val currentUserId = localCache.getCurrentUser()?.id ?: ""
        if (currentUserId.isEmpty()) return emptyList()

        return when (val token = kVault.getAuthToken()) {
            is String -> {
                try {
                    client
                        .get("http://localhost.proxyman.io:8080/chats") {
                            bearerAuth(token)
                            accept(ContentType.Application.Json)
                            parameter("userId", currentUserId)
                        }
                        .body<List<ChatUnit>>()
                } catch (e: Exception) {
                    emptyList()
                }
            }
            else -> emptyList()
        }
    }

    override suspend fun getAllMessages(chatId: String): List<MessageUnit> = when (
        val token = kVault.getAuthToken()
    ) {
        is String -> {
            try {
                client.get(
                    "http://localhost.proxyman.io:8080/chats/getMessages"
                ) {
                    bearerAuth(token)
                    header("sender_id", localCache.getCurrentUser()?.id)
                    parameter("chatId", chatId)
                }.body<List<MessageUnit>>()
            } catch (e: Exception) {
                println(e)
                emptyList()
            }
        }
        else -> emptyList()
    }
}

fun ChatUnit.Companion.chatsUnitMock() = (0..<30).toList().map { ChatUnit(id = "$it", name = "Name", ownerId = "${it * 2}") }

@Serializable
data class CreateFaceToFaceChatRequest(
    val name: String,
    val ownerId: String,
    val opponent: String,
)

@Serializable
data class MessageUnit(
    val chatId: String,
    val message: String,
    val senderId: String
)