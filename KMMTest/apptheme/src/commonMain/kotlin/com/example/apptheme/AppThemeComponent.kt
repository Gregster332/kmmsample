package com.example.apptheme

import com.arkivanov.decompose.ComponentContext
import com.example.core.koin.ComponentKoinContext

class AppThemeComponent(
    componentContext: ComponentContext,
    private val onDismiss: () -> Unit
): AppTheme, ComponentContext by componentContext {
    override fun makeGreetText(): String {
        return "Hello"
    }

    override fun dismiss() {
        onDismiss()
    }
}