package com.example.mykmmtest.android

import android.app.Application
import android.content.SharedPreferences
import com.example.mykmmtest.DI.initKoin
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

class Application: Application() {
    override fun onCreate() {
        super.onCreate()
        Firebase.initialize(this)
        initKoin(
            appDeclaration = module {
                single<SharedPreferences> {
                    androidContext().getSharedPreferences(
                        "KAMPSTARTER_SETTINGS",
                        MODE_PRIVATE
                    )
                }
            },
            block = { androidContext(this@Application) }
        )
    }
}