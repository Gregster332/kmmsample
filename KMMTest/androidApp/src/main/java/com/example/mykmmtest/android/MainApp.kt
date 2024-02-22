package com.example.mykmmtest.android

import android.app.Application
import com.example.corenetwork.api.devicesensorsapi.DeviceSensorApi
import com.example.mykmmtest.android.platform.DeviceSensorApiImpl
import com.example.mykmmtest.di.initKoin
import org.koin.android.ext.koin.androidContext

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
