package com.example.mykmmtest.Services.SocketServiceImpl

//import com.example.corenetwork.api.SecurePersistant.SettingsKeys
import com.example.corenetwork.api.SecurePersistant.SettingsPersistent
import com.example.mykmmtest.Services.PlatformListener
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual class SocketServiceImpl actual constructor(
    url: String
): KoinComponent {
    private val socketEndpoint = url
    private var webSocket: WebSocket? = null

    private val settings by inject<SettingsPersistent>()

    actual fun open(listener: PlatformListener) {
        val token = settings.getString("AUTH_TOKEN")

        val socketRequest = Request.Builder()
            .url(socketEndpoint)
            .header("Authorization", "Bearer $token")
            .build()

        val webClient = OkHttpClient().newBuilder().build()
        webSocket = webClient.newWebSocket(
            socketRequest,
            object : okhttp3.WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) = listener.onOpen()
                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) = listener.onFailure(t)
                override fun onMessage(webSocket: WebSocket, text: String) = listener.onMessage(text)
                override fun onClosing(webSocket: WebSocket, code: Int, reason: String) = listener.onClose()
                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) = listener.onClose()
            }
        )
    }

    actual fun close() {
        webSocket?.close(code = 404, reason = "")
        webSocket = null
    }

    actual fun sendMessage(s: String) {
        webSocket?.send(s)
    }
}