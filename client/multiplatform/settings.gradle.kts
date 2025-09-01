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

include(":divkit-multiplatform")
include(":sample:app")
