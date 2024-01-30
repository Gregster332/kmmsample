object Dependencies {
    object Kotlin {
        object Coroutines {
            private const val version = "1.7.3"
            const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
        }

        object Serialize {
            const val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0"
        }

        object Ktor {
            private const val version = "2.3.4"
            const val core = "io.ktor:ktor-client-core:$version"
            const val okHttp = "io.ktor:ktor-client-okhttp:$version"
            const val darwin = "io.ktor:ktor-client-darwin:$version"
            const val webSocket = "io.ktor:ktor-client-websockets:$version"
            const val negotiation = "io.ktor:ktor-client-content-negotiation:$version"
            const val json = "io.ktor:ktor-serialization-kotlinx-json:$version"
        }

        object SQL {
            const val runtime = "com.squareup.sqldelight:runtime:1.5.5"
            const val android = "app.cash.sqldelight:android-driver:2.0.0"
            const val ios = "app.cash.sqldelight:native-driver:2.0.0"
        }
    }

    object MVI {
        private const val version = "3.0.2"
        const val core = "com.arkivanov.mvikotlin:mvikotlin:$version"
        const val main = "com.arkivanov.mvikotlin:mvikotlin-main:$version"
        const val logging = "com.arkivanov.mvikotlin:mvikotlin-logging:$version"
        const val coroutines = "com.arkivanov.mvikotlin:mvikotlin-extensions-coroutines:$version"
    }

    object Koin {
        private const val version = "3.1.5"
        const val core = "io.insert-koin:koin-core:$version"
        const val android = "io.insert-koin:koin-android:$version"
    }

    object KmmViewModel {
        private const val version = "0.16.1"
        const val core = "dev.icerock.moko:mvvm-core:$version"
        const val cFlow = "dev.icerock.moko:mvvm-flow:$version"
    }

    object Settings {
        private const val version = "1.1.1"
        const val settings = "com.russhwolf:multiplatform-settings-no-arg:$version"
    }

    object Decompose {
        private const val version = "2.1.4"
        const val decompose = "com.arkivanov.decompose:decompose:$version"
        const val jetpack = "com.arkivanov.decompose:extensions-compose-jetpack:$version"
        //const val jetpack = "com.arkivanov.decompose:extensions-compose-jetpack:$version"
    }
}