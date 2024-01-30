package com.example.corenetwork.api.Chats

import com.example.corenetwork.model.Chats.Chat
import com.example.corenetwork.model.Chats.ChatUnit
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.path
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent

internal class ChatsApiImpl(
    private val client: HttpClient
): ChatsApi {
//    override suspend fun getAllUserChats(): List<> {
//        val response = client
//            .get("ws://localhost.proxyman.io:8080/getAll")
//            .bodyAsText()
//        return Json{ ignoreUnknownKeys = true }.decodeFromString<List<ChatUnit>>(response)
//    }
}

@Serializable
data class Chat(
    @SerialName("id")
    val id: String,

    @SerialName("name")
    val name: String,

    @SerialName("chat_type")
    val chatType: Long
)
