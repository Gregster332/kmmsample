package com.example.mykmmtest.Services.SocketServiceImpl

import com.example.mykmmtest.Services.PlatformListener
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import platform.Foundation.NSData
import platform.Foundation.NSOperationQueue.Companion.currentQueue
import platform.Foundation.NSURL
import platform.Foundation.NSURLSession
import platform.Foundation.NSURLSessionConfiguration
import platform.Foundation.NSURLSessionWebSocketCloseCode
import platform.Foundation.NSURLSessionWebSocketDelegateProtocol
import platform.Foundation.NSURLSessionWebSocketMessage
import platform.Foundation.NSURLSessionWebSocketTask
import platform.Foundation.setValue
import platform.darwin.NSObject

actual class SocketServiceImpl actual constructor(
    url: String
): KoinComponent {
    private val socketEndpoint = NSURL.URLWithString(url)!!
    private var webSocket: NSURLSessionWebSocketTask? = null

    //private val settings by inject<SecurePersistant>()

    actual fun open(listener: PlatformListener) {
        val urlSession = NSURLSession.sessionWithConfiguration(
            configuration = NSURLSessionConfiguration.defaultSessionConfiguration(),
            delegate = object : NSObject(), NSURLSessionWebSocketDelegateProtocol {
                override fun URLSession(
                    session: NSURLSession,
                    webSocketTask: NSURLSessionWebSocketTask,
                    didOpenWithProtocol: String?
                ) {
                    listener.onOpen()
                }
                override fun URLSession(
                    session: NSURLSession,
                    webSocketTask: NSURLSessionWebSocketTask,
                    didCloseWithCode: NSURLSessionWebSocketCloseCode,
                    reason: NSData?
                ) {
                    listener.onClose()
                }
            },
            delegateQueue = currentQueue()
        )

//        val token = settings.getValue("auth_token") ?: ""
//        socketEndpoint.setValue("Bearer $token", "Authorization")

        webSocket = urlSession.webSocketTaskWithURL(socketEndpoint)
        listenMessages(listener)
        webSocket?.resume()
    }

    actual fun close() {
        webSocket?.cancel()
        webSocket = null
    }

    actual fun sendMessage(s: String) {
        val message = NSURLSessionWebSocketMessage(string = s)
        webSocket?.sendMessage(message) { error ->

        }
    }

    private fun listenMessages(listener: PlatformListener) {
        webSocket?.receiveMessageWithCompletionHandler { message, error ->
            when {
                error != null -> {
                    webSocket?.cancel()
                    webSocket = null
                    listener.onFailure(Throwable(error.description))
                }
                message != null -> {
                   message.string?.let { listener.onMessage(string = it) }
                }
            }
            listenMessages(listener)
        }
    }
}