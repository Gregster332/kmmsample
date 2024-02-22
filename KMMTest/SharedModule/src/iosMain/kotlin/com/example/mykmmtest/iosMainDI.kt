package com.example.mykmmtest

import com.example.corenetwork.api.devicesensorsapi.DeviceSensorApi
import com.example.mykmmtest.di.initKoin

fun initKoinIOS(deviceSensorApi: DeviceSensorApi) = initKoin(deviceSensorApi = deviceSensorApi)