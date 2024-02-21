package com.example.backend.authorization.Tokens

import java.util.Collections

class RefreshTokenRepository {
    private val tokens = Collections.synchronizedMap<String, String>(mutableMapOf())

    fun findUsernameByToken(token: String): String? = tokens[token]

    fun save(token: String, userId: String) {
        tokens[token] = userId
    }
}
