@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

plugins {
    id("divkit.convention.library-kmp")
    id("divkit.convention.publishing-module-kmp")
    id("divkit.convention.jvm-api-check")
}

kotlin {
    jvm()

    android {
        // Keep generated Android classes separate from the compatibility module.
        namespace = "com.yandex.div.storage.kmp"
    }

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    applyDefaultHierarchyTemplate {
        common {
            group("jvmAndAndroid") {
                withJvm()
                withCompilations { it.platformType == KotlinPlatformType.androidJvm }
            }
        }
    }

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":core"))
            api(libs.androidx.sqlite)
            api(libs.androidx.annotation)
            implementation(libs.kotlinx.atomicfu)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.androidx.sqlite.bundled)
        }
    }
}
