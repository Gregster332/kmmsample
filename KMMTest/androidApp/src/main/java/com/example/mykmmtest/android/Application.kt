package com.example.mykmmtest.android

import android.app.Application
import com.example.mykmmtest.DI.initKoin
import org.koin.android.ext.koin.androidContext

class Application: Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin() {
            androidContext(applicationContext)
        }
    }
}