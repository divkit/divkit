@file:Suppress("UnstableApiUsage")

rootProject.name = "divkit-multiplatform"

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

include(":sample:composeApp")
include(":divkit-multiplatform")
