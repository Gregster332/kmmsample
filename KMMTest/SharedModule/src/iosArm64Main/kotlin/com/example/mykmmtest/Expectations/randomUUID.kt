package com.example.mykmmtest.Expectations

import platform.Foundation.NSUUID

actual fun randomUUID(): String = NSUUID().UUIDString()