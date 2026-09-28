plugins {
    alias(libs.plugins.android.library)
    id("divkit.convention.abi-validation")
}

apply(from = "../div-library.gradle")
apply(from = "../publish-android.gradle")

android {
    namespace = "com.yandex.div.network"
}

dependencies {
    implementation(project(":div"))

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.okhttp)
}
