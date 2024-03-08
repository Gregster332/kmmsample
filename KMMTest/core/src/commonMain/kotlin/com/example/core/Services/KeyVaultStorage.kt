package com.example.core.Services

import com.liftric.kvault.KVault
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface KeyVaultStorage {
    fun setRefreshToken(token: String)
    fun getRefreshToken(): String?
    fun setAuthToken(token: String)
    fun getAuthToken(): String?
    fun clear()
}

internal class KeyVaultStorageImpl(

): KeyVaultStorage, KoinComponent {

    private val kVault: KVault by inject()

    override fun setRefreshToken(token: String) {
        kVault.set(refreshTokenKey, token)
    }

    override fun getRefreshToken() = kVault.string(refreshTokenKey)

    override fun setAuthToken(token: String) {
        kVault.set(authTokenKey, token)
    }

    override fun getAuthToken(): String? = kVault.string(authTokenKey)

    override fun clear() {
        listOf(refreshTokenKey, authTokenKey).forEach {
            kVault.deleteObject(it)
        }
    }

    companion object {
        const val refreshTokenKey = "refresh_token_key"
        const val authTokenKey = "auth_token_key"
    }
}

internal object KeyVaultConstants {
    val keyVaultStorageIosServiceName = "kvault_ios_service_name"
}