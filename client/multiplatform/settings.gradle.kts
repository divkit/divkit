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
        providers.gradleProperty("divkitMavenRepository").orNull?.let { repository ->
            exclusiveContent {
                forRepository { maven(repository) }
                filter { includeGroup("com.yandex.divkit.multiplatform") }
            }
        }
        google()
        mavenCentral()
    }
}

include(":divkit-multiplatform")
include(":sample:app")
