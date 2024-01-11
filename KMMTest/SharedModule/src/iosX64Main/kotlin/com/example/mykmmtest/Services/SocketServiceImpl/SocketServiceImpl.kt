package com.example.mykmmtest.Services.SocketServiceImpl

import com.example.mykmmtest.Services.PlatformListener

actual class SocketServiceImpl actual constructor(url: String) {
    actual fun open(listener: PlatformListener) {
    }

    actual fun close() {
    }

    actual fun sendMessage(s: String) {
    }
}