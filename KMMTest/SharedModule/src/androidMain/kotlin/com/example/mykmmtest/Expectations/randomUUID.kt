package com.example.mykmmtest.Expectations

import java.util.UUID

actual fun randomUUID(): String = UUID.randomUUID().toString()