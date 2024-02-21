plugins {
    kotlin("multiplatform")
    id("com.android.library")
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

    //applyDefaultHierarchyTemplate()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(Dependencies.MVI.core)
                implementation(Dependencies.MVI.main)
                implementation(Dependencies.MVI.coroutines)
                implementation(Dependencies.Kotlin.Coroutines.core)
                implementation(Dependencies.Koin.core)
                api("com.arkivanov.essenty:lifecycle:1.2.0")
                api("com.liftric:kvault:1.12.0")
                api(Dependencies.Settings.settings)
                implementation("com.russhwolf:multiplatform-settings-coroutines:1.1.1")
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
    namespace = "com.example.core"
    compileSdk = 34
    defaultConfig {
        minSdk = 31
        targetSdk = 34
    }
}