package com.example.mykmmtest.android

import android.app.Application
import android.content.Context
import com.example.corenetwork.api.devicesensorsapi.DeviceSensorApi
import com.example.mykmmtest.android.platform.DeviceSensorApiImpl
import com.example.mykmmtest.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

class MainApp : Application() {

    private val deviceSensorApi: DeviceSensorApi = DeviceSensorApiImpl(this)

    override fun onCreate() {
        super.onCreate()
        initKoin(
            block = {
                androidContext(this@MainApp)
            },
            deviceSensorApi = deviceSensorApi
        )
    }
}
