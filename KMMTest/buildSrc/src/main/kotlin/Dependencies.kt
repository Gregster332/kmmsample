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
            const val ios = "app.cash.sqldelight:native-driver:2.0.1"
            const val coroutines = "app.cash.sqldelight:coroutines-extensions:2.0.1"
        }
    }

    object MVI {
        private const val version = "4.0.0-alpha01"
        const val core = "com.arkivanov.mvikotlin:mvikotlin:$version"
        const val main = "com.arkivanov.mvikotlin:mvikotlin-main:$version"
        const val logging = "com.arkivanov.mvikotlin:mvikotlin-logging:$version"
        const val coroutines = "com.arkivanov.mvikotlin:mvikotlin-extensions-coroutines:$version"
    }

    object Lifecycle {
        private const val version = "2.0.0-alpha02"
        const val main = "com.arkivanov.essenty:lifecycle:$version"
    }

    object Koin {
        private const val version = "3.1.5"
        const val core = "io.insert-koin:koin-core:$version"
        const val android = "io.insert-koin:koin-android:$version"
    }

    object Settings {
        private const val version = "1.1.1"
        const val settings = "com.russhwolf:multiplatform-settings-no-arg:$version"
    }

    object Decompose {
        private const val version = "3.0.0-alpha02"
        const val decompose = "com.arkivanov.decompose:decompose:$version"
        const val jetpack = "com.arkivanov.decompose:extensions-compose:$version"
    }

    object Utils {
        object DateTime {
            private const val version = "0.5.0"
            const val dateTime = "org.jetbrains.kotlinx:kotlinx-datetime:$version"
        }

        object Resources {
            private const val version = "0.23.0"
            const val main = "dev.icerock.moko:resources:$version"
        }
    }
}
