package com.example.corenetwork.api.auth

import com.example.corenetwork.model.auth.SignUpRequestEntity

interface AuthApi {
    suspend fun generateToken(entity: SignUpRequestEntity): TokensResponse?
    suspend fun trySignInWithToken(): UserLogInInfo
    suspend fun refreshIdToken()
    suspend fun logOut(): Boolean
}
