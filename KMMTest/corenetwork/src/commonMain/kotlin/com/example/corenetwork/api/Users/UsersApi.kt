package com.example.corenetwork.api.Users

import com.example.corenetwork.api.Auth.UserBaseInfo
import com.example.corenetwork.api.SecurePersistant.SettingsPersistent
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

open class NetworkException(message: String): Exception(message)
//internal class

interface UsersApi {
    suspend fun getAllUsers(useCache: Boolean): List<UserBaseInfo>
}

internal class UsersApiImpl(
    private val httpClient: HttpClient,
    private val syncService: SyncService,
    private val settings: SettingsPersistent
): UsersApi {
    override suspend fun getAllUsers(useCache: Boolean): List<UserBaseInfo> = syncService.sync(
        useCache,
        CacheKeys.getAllUsersCacheKey
    ) {
        getAllUsers()
    }

    private suspend fun getAllUsers(): List<UserBaseInfo> = when(
        val token = settings.getString("AUTH_TOKEN")
    ) {
        is String -> httpClient.get("http://localhost.proxyman.io:8080/users") {
            headers.append("Authorization", "Bearer ${token}")
        }.body()
        else -> { emptyList() }
    }

    private object CacheKeys {
        const val getAllUsersCacheKey = "k_get_all_users"
    }
}

internal class SyncService {
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