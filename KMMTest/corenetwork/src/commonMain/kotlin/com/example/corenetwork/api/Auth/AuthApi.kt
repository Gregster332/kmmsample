package com.example.corenetwork.api.Auth

import com.example.corenetwork.model.Auth.SignUpRequestEntity
import com.example.corenetwork.model.Auth.AuthState

interface AuthApi {
    suspend fun generateToken(entity: SignUpRequestEntity)
    suspend fun trySignInWithToken(): AuthState
    suspend fun refreshIdToken()
}