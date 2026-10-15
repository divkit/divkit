plugins {
    alias(libs.plugins.android.library)
    id("divkit.convention.abi-validation")
}

apply(from = "../div-library.gradle")
apply(from = "../publish-android.gradle")

android {
    namespace = "com.yandex.div.network.okhttp"

    testOptions.unitTests.all {
        // A single blocking body read must exhaust IO capacity to test cancellation under saturation.
        it.systemProperty("kotlinx.coroutines.io.parallelism", "1")
    }
}

dependencies {
    implementation(project(":div-core"))

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.okhttp)

    testImplementation(libs.kotlin.test)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.okhttp.mockwebserver)
}
