package com.example.mykmmtest.Services.SocketServiceImpl

import com.example.mykmmtest.Services.PlatformListener

expect class SocketServiceImpl(
    url: String
) {
    fun open(listener: PlatformListener)
    fun close()
    fun sendMessage(s: String)
}