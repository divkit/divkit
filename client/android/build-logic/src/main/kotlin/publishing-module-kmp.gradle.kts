package divkit.convention

import com.yandex.div.gradle.configurePublication

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    `maven-publish`
    signing
}

configurePublication()
