package com.example.corenetwork.api.auth

import com.example.corenetwork.model.auth.SignUpRequestEntity

interface AuthApi {
    suspend fun generateToken(entity: SignUpRequestEntity): Boolean
    suspend fun trySignInWithToken(): UserLogInInfo
    suspend fun refreshIdToken()
}
