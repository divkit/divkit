@file:Suppress("UnstableApiUsage")

pluginManagement {
    val internalRepositories = file("../repositories.settings.internal.gradle")
    if (internalRepositories.exists()) {
        settings.apply(from = internalRepositories)
    }
    repositories {
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
    repositories {
        google()
        mavenCentral()
    }
}
