plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    kotlin("plugin.serialization") version "1.9.10"
    id("kotlin-parcelize")
    id("dev.icerock.mobile.multiplatform-resources")
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
            export(Dependencies.Decompose.decompose)
            export("dev.icerock.moko:resources:0.23.0")
            export("dev.icerock.moko:graphics:0.9.0")
            export("com.arkivanov.essenty:lifecycle:1.2.0")
            export(project(":chats"))
            export(project(":core"))
            export(project(":corenetwork"))
            export(project(":apptheme"))
            export(project(":searchlist"))
        }
        //extraSpecAttributes["resources"] = "['src/commonMain/resources/**']"
    }
    
    sourceSets {
        val androidMain by getting {
            dependsOn(commonMain.get())
            dependencies {
                implementation(Dependencies.Kotlin.Ktor.okHttp)
                implementation(Dependencies.Kotlin.SQL.android)
                implementation(Dependencies.Koin.android)
            }
        }

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
                api(Dependencies.Decompose.decompose)
                api("com.arkivanov.essenty:lifecycle:1.2.0")
                api("dev.icerock.moko:resources:0.23.0")
                implementation(project(":chats"))
                implementation(project(":apptheme"))
                implementation(project(":core"))
                implementation(project(":corenetwork"))
                implementation(project(":searchlist"))
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val iosX64Main by getting {
            //resources.srcDirs("build/generated/moko/iosX64Main/src")
        }
        val iosArm64Main by getting {
            //resources.srcDirs("build/generated/moko/iosArm64Main/src")
        }
        val iosSimulatorArm64Main by getting {
            //resources.srcDirs("build/generated/moko/iosSimulatorArm64Main/src")
        }

        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                implementation(Dependencies.Kotlin.Ktor.darwin)
                implementation(Dependencies.Kotlin.SQL.ios)
                api(Dependencies.Decompose.decompose)
                api("dev.icerock.moko:resources:0.23.0")
                api(project(":chats"))
                api(project(":core"))
                api(project(":corenetwork"))
                api(project(":apptheme"))
                api(project(":searchlist"))
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

//    sourceSets["main"].apply {
//        //manifest.srcFile("src/androidMain/AndroidManifest.xml")
//        //res.srcDirs("src/androidMain/resources")
//        resources.srcDirs("src/commonMain/resources")
//        java.srcDirs("build/generated/moko/androidMain/src")
//    }
}

multiplatformResources {
    multiplatformResourcesPackage = "com.example.mykmmtest"
}