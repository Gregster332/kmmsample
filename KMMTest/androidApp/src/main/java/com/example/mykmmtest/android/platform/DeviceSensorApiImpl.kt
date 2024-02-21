package com.example.mykmmtest.android.platform

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.corenetwork.api.devicesensorsapi.DeviceSensorApi
import kotlin.math.sqrt

class DeviceSensorApiImpl(
    private val context: Context
): DeviceSensorApi, SensorEventListener {

    private lateinit var callback: () -> Unit
    private lateinit var sensorManager: SensorManager
    private var sensor: Sensor? = null

    private var acceleration = 0f
    private var currentAcceleration = 0f
    private var lastAcceleration = 0f

    override fun start() {
        if (!::sensorManager.isInitialized) {
            sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        }
        sensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun stop() {
        if (!::sensorManager.isInitialized) return
        sensorManager.unregisterListener(this)
    }

    override fun setSensorListener(listener: () -> Unit) {
        callback = listener
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val x = (event?.values?.get(0) ?: 0).toFloat()
        val y = (event?.values?.get(1) ?: 0).toFloat()
        val z = (event?.values?.get(2) ?: 0).toFloat()
        lastAcceleration = currentAcceleration

        currentAcceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
        val delta: Float = currentAcceleration - lastAcceleration
        acceleration = acceleration * 0.9f + delta

        if (acceleration > 12) {
            callback()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}