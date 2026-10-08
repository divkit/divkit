@file:Suppress("UnstableApiUsage")

plugins {
    id("divkit.convention.library-kmp")
    id("divkit.convention.publishing-module-kmp")
}

kotlin {
    jvm()

    android {
        // '.kmp' suffix used to resolve conflict with :div-core module.
        namespace = "com.yandex.div.core.kmp"
    }

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.kotlinx.datetime)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
