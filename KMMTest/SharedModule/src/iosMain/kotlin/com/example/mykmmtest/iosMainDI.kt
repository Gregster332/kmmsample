package com.example.mykmmtest

import com.example.mykmmtest.Storiess.Auth.AuthViewModel
import com.example.mykmmtest.Storiess.Main.ViewModel.MainViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class IosMainDI: KoinComponent {
    fun mainViewModel(): MainViewModel = get()
    fun authViewModel(): AuthViewModel = get()
}