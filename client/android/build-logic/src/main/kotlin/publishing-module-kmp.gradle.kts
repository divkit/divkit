package divkit.convention

import com.yandex.div.gradle.mavenPublication

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("divkit.convention.abi-validation")
    `maven-publish`
    signing
}

mavenPublication()
