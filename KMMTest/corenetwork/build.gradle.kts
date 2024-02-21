plugins {
    kotlin("multiplatform")
    id("com.android.library")
    kotlin("plugin.serialization") version "1.9.10"
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":core"))
                implementation(Dependencies.Koin.core)
                implementation(Dependencies.Kotlin.Ktor.core)
                implementation(Dependencies.Kotlin.Ktor.json)
                implementation(Dependencies.Kotlin.Ktor.negotiation)
                implementation(Dependencies.Kotlin.Ktor.webSocket)
                implementation(Dependencies.Kotlin.Coroutines.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
    }
}

android {
    namespace = "com.example.corenetwork"
    compileSdk = 34
    defaultConfig {
        minSdk = 31
        targetSdk = 34
    }
}