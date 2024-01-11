plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    kotlin("plugin.serialization") version "1.9.10"
    id("kotlin-parcelize")
    id("app.cash.sqldelight") version "2.0.0"
}

kotlin {
   //targetHierarchy.default()

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

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "16.0"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "SharedModule"

            export(Dependencies.KmmViewModel.cFlow)
            export(Dependencies.KmmViewModel.core)
            export(Dependencies.Decompose.decompose)
            export("com.arkivanov.essenty:lifecycle:1.2.0")
        }
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(Dependencies.Kotlin.Ktor.core)
                implementation(Dependencies.Kotlin.Ktor.webSocket)
                implementation(Dependencies.Kotlin.Serialize.serialization)
                implementation(Dependencies.Kotlin.Ktor.negotiation)
                implementation(Dependencies.Kotlin.Ktor.json)
                implementation(Dependencies.Kotlin.Coroutines.core)
                implementation(Dependencies.Kotlin.SQL.runtime)
                implementation(Dependencies.MVI.core)
                implementation(Dependencies.MVI.coroutines)
                implementation(Dependencies.MVI.logging)
                implementation(Dependencies.MVI.main)
                implementation(Dependencies.Koin.core)
                api(Dependencies.KmmViewModel.core)
                api(Dependencies.KmmViewModel.cFlow)
                implementation(Dependencies.Settings.settings)
                api(Dependencies.Decompose.decompose)
                api("com.arkivanov.essenty:lifecycle:1.2.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(Dependencies.Kotlin.Ktor.okHttp)
                implementation(Dependencies.Kotlin.SQL.android)
                implementation(Dependencies.Koin.android)
            }
        }

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting

        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                implementation(Dependencies.Kotlin.Ktor.darwin)
                implementation(Dependencies.Kotlin.SQL.ios)
                api(Dependencies.Decompose.decompose)
            }
        }
    }
}

android {
    namespace = "com.example.mykmmtest"
    compileSdk = 34
    defaultConfig {
        minSdk = 31
    }
}

sqldelight {
    databases {
        create("CoreDB") {
            packageName.set("com.example.app.core.database")
        }
    }
}