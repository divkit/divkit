plugins {
    alias(libs.plugins.android.library)
    id("divkit.convention.abi-validation")
}

apply(from = "../div-library.gradle")
apply(from = "../div-tests.gradle")
apply(from = "../publish-android.gradle")

android {
    namespace = "com.yandex.divkit.backdrop"
}

dependencies {
    implementation(project(":div"))
    implementation(project(":div-core"))

    implementation(libs.androidx.core)
}
