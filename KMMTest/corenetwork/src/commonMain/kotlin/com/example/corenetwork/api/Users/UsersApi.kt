package com.example.corenetwork.api.Users

import com.example.corenetwork.api.Auth.LocalCache
import com.example.corenetwork.api.Auth.UserBaseInfo
import com.example.corenetwork.api.SecurePersistant.SettingsPersistent
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

open class NetworkException(message: String): Exception(message)

interface UsersApi {
    suspend fun getAllUsers(): List<UserBaseInfo>
    suspend fun searchUsersBy(nickname: String): List<UserBaseInfo>
}

internal class UsersApiImpl(
    private val httpClient: HttpClient,
    private val settings: SettingsPersistent
): UsersApi {
//    override suspend fun getAllUsers(useCache: Boolean): List<UserBaseInfo> {
//        val users = getAllUsers()
//        return if (users.isNotEmpty()) {
//            users.forEach { localCache.saveNewUser(it) }
//            users
//        } else {
//            emptyList()
//        }
//    }

    override suspend fun getAllUsers(): List<UserBaseInfo> = when(
        val token = settings.getString("AUTH_TOKEN")
    ) {
        is String -> httpClient.get("http://localhost.proxyman.io:8080/users") {
            headers.append("Authorization", "Bearer ${token}")
        }.body()
        else -> { emptyList() }
    }

    override suspend fun searchUsersBy(nickname: String): List<UserBaseInfo> = when(
        val token = settings.getString("AUTH_TOKEN")
    ) {
        is String -> httpClient.get("http://localhost.proxyman.io:8080/users/bysearch"){
            headers.append("Authorization", "Bearer ${token}")
            parameter("search_text", nickname)
        }.body()
        else -> { emptyList() }
    }
}

internal object SyncService {
    suspend inline fun <reified R> sync(
        useCache: Boolean = true,
        key: String,
        block: suspend () -> R
    ): R {
        if (useCache && LocalCacheManager.get(key) != null) {
            return LocalCacheManager.get(key).let {
                if (it is R) {
                    it
                } else {
                    throw NetworkException("")
                }
            }
        }

        return try {
            val result = block()
            LocalCacheManager.save(key, result)
            result
        } catch (e: Exception) {
            throw e
        }
    }
}

object LocalCacheManager {
    private val map = HashMap<String, Any?>()

    fun save(key: String, data: Any?) {
        map[key] = data
    }

    fun get(key: String): Any? = map[key]
}