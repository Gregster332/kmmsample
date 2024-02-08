pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "MyKMMTest"
include(":androidApp")
include(":SharedModule")
include(":backend")
include(":chats")
include(":core")
include(":corenetwork")
include(":apptheme")
include(":searchlist")
include(":authentication")
