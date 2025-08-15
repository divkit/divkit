@Suppress("UnstableApiUsage")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

rootProject.name = "kotlin-json-builder"

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}

include(":divan-json-builder")
include(":legacy-json-builder")
