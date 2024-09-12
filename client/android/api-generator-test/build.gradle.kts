import groovy.json.JsonOutput

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.apiGenerator)
}

apply(from = "${projectDir}/../div-library.gradle")
apply(from = "${projectDir}/../div-tests.gradle")

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjvm-default=all")
        languageVersion = "1.6"
        apiVersion = "1.6"
    }
}

val testDataLocation = file("../../../test_data")

schemas {
    create("testData") {
        config.set(file("testing-generator-config.json"))
        schemas.set(testDataLocation)
    }
}

android {
    namespace = "com.yandex.generator"

    buildFeatures { buildConfig = true }

    defaultConfig {
        buildConfigField("String", "TEMPLATES_JSON_PATH", JsonOutput.toJson(testDataLocation.path))
    }

    testOptions {
        unitTests {
            all {
                it.jvmArgs = listOf("-noverify")
                it.testLogging {
                    events("passed", "skipped", "failed", "standardOut", "standardError")
                }
            }
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(project(":assertion"))
    implementation(project(":div-core"))
    implementation(project(":div-json"))
    implementation(project(":div-data"))
    implementation(project(":utils"))
}
