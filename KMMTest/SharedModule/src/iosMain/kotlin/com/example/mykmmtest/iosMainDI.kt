package com.example.mykmmtest

import com.example.mykmmtest.Stories.Main.Model.ViewModel.MainViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class IosMainDI: KoinComponent {
    fun mainViewModel(): MainViewModel = get()
}