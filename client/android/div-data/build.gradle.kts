import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.apiGenerator)
}

apply(from = "../div-library.gradle")
apply(from = "../div-tests.gradle")
apply(from = "../publish-android.gradle")

android {
    namespace = "com.yandex.div.data"
}

dependencies {
    implementation(project(":assertion"))
    implementation(project(":div-core"))
    implementation(project(":div-evaluable"))
    implementation(project(":logging"))
    implementation(project(":utils"))

    api(libs.androidx.core)

    testImplementation(libs.kotlin.reflect)
}

schemas {
    create("divModel") {
        schemas.set(file("../../../schema"))
        config.set(file("div2-generator-config.json"))
    }
    create("sharedData") {
        schemas.set(file("../../../shared_data"))
        config.set(file("div2-shared-data-generator-config.json"))
    }
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
    }
}
