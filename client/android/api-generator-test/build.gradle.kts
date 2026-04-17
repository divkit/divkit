import groovy.json.JsonOutput

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.apiGenerator)
}

apply(from = "${projectDir}/../div-library.gradle")
apply(from = "${projectDir}/../div-tests.gradle")

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
}

dependencies {
    implementation(project(":assertion"))
    implementation(project(":div-core"))
    implementation(project(":div-data"))
    implementation(project(":utils"))
}
