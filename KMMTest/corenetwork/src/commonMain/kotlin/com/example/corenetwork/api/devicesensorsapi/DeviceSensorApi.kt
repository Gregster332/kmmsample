package com.example.corenetwork.api.devicesensorsapi

// TOD0: - Нафиг вынести отсюда надо. Но пока не знаю куда...
public interface DeviceSensorApi {
    fun start()
    fun stop()
    fun setSensorListener(listener: () -> Unit)
}