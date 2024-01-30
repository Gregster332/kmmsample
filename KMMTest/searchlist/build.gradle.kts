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

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":core"))
                implementation(project(":corenetwork"))
                implementation(Dependencies.Koin.core)
                implementation(Dependencies.Kotlin.Ktor.core)
                implementation(Dependencies.Decompose.decompose)
                implementation(Dependencies.Kotlin.Serialize.serialization)
                implementation(Dependencies.Kotlin.Coroutines.core)
                implementation(Dependencies.MVI.core)
                implementation(Dependencies.MVI.main)
                implementation(Dependencies.MVI.coroutines)
            }
        }
//        val commonTest by getting {
//            dependencies {
//                implementation(kotlin("test"))
//            }
//        }
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
    namespace = "com.example.chats"
    compileSdk = 34
    defaultConfig {
        minSdk = 31
        targetSdk = 34
    }
}