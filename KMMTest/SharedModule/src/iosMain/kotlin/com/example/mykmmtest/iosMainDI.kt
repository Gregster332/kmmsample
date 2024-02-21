package com.example.mykmmtest

import app.cash.sqldelight.db.SqlDriver
import com.example.corenetwork.api.devicesensorsapi.DeviceSensorApi
import com.example.mykmmtest.di.initKoin
import com.example.mykmmtest.utils.DriverFactory
import com.russhwolf.settings.KeychainSettings
import org.koin.dsl.module
import platform.Foundation.NSNotificationCenter

fun initKoinIOS(deviceSensorApi: DeviceSensorApi) = initKoin(deviceSensorApi = deviceSensorApi)