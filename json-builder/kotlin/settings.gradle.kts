@file:Suppress("UnstableApiUsage")

rootProject.name = "kotlin-json-builder"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}

include(":core")
include(":divan-json-builder")
include(":expression-dsl")
include(":expression-dsl-compat")
include(":legacy-json-builder")
include(":lint-rules")
