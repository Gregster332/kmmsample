package com.example.corenetwork.model.chats

import kotlinx.serialization.Serializable

@Serializable
data class User(val name: String, val age: Int)
