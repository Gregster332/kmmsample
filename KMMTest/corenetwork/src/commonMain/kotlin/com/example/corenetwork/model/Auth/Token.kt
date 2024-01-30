package com.example.corenetwork.model.Auth

import kotlinx.serialization.Serializable

@Serializable
data class Token(
    val token: String
)