package divkit.convention

import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("com.android.kotlin.multiplatform.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("divkit.convention.abi-validation")
}

val libs = the<LibrariesForLibs>()
val jvmToolchainVersion = libs.versions.jvm.toolchain.map(String::toInt)
val androidCompileSdk = libs.versions.android.compileSdk.map(String::toInt)
val androidMinSdk = libs.versions.android.minSdk.map(String::toInt)

// TODO(gulevsky): remove withGroovyBuilder once com.yandex.div.gradle.Version class becomes available
val divkitVersion: Any by rootProject.extra
version = divkitVersion.withGroovyBuilder {
    getProperty("baseVersionName") as String
}

kotlin {
    jvmToolchain(jvmToolchainVersion.get())

    explicitApi()

    android {
        compileSdk = androidCompileSdk.get()
        minSdk = androidMinSdk.get()
    }
}

dependencies {
    add("lintChecks", project(":lint-rules"))
}
