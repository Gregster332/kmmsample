package com.example.mykmmtest.Services.SecurePersistant

import org.koin.core.component.KoinComponent

expect class SecurePersistantImpl: SecurePersistant, KoinComponent

interface SecurePersistant {
    fun getValue(key: String): String?
    fun set(value: String?, key: String)
    fun clear()
}

