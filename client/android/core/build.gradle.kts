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
            api(libs.kotlinx.serialization.json)
            compileOnly(libs.json)
        }
        jvmMain.dependencies {
            api(libs.json)
        }
        commonTest.dependencies {
            implementation(libs.json)
            implementation(libs.kotlin.test)
        }
    }
}
