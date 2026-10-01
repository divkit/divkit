@file:Suppress("UnstableApiUsage")

import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.BOOLEAN
import com.yandex.div.gradle.disableLogs

plugins {
    id("divkit.convention.library-kmp")
    id("divkit.convention.publishing-module-kmp")
    alias(libs.plugins.buildkonfig)
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
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

buildkonfig {
    packageName = "com.yandex.div.core"

    defaultConfigs {
        buildConfigField(BOOLEAN, "DISABLE_LOGS", disableLogs().toString())
    }
}

tasks.named("metalavaGenerateSignature") {
    dependsOn("generateBuildKonfig")
}

tasks.named("metalavaCheckCompatibility") {
    dependsOn("generateBuildKonfig")
}
