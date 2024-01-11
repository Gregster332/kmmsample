package com.example.mykmmtest.Storiess.Splash

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.navigate
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

class SplashComponent constructor(
    componentContext: ComponentContext,
): Splash, ComponentContext by componentContext {

    private val navigation = StackNavigation<Configuration>()
    override val childStack: Value<ChildStack<*, Splash.Child>> = childStack(
        source = navigation,
        initialConfiguration = Configuration.Main,
        handleBackButton = true,
        childFactory = ::createChild
    )

    override fun onBackPressed(): Unit {
        navigation.pop()
    }

    override fun navigateAuth() {
        navigation.push(Configuration.Auth)
    }

    private fun createChild(config: Configuration, componentContext: ComponentContext): Splash.Child =
        when (config) {
            is Configuration.Main -> Splash.Child.Main
            is Configuration.Auth -> Splash.Child.Auth
        }

    private sealed class Configuration: Parcelable {
        @Parcelize
        data object Main: Configuration()

        @Parcelize
        data object Auth: Configuration()
    }
}

interface Splash {
    val childStack: Value<ChildStack<*, Child>>

    fun onBackPressed()
    fun navigateAuth()

    sealed class Child {
        data object Main: Child()
        data object Auth: Child()
    }
}