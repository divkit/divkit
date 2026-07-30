plugins {
    alias(libs.plugins.android.library)
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
    implementation(libs.androidx.coreKtx)
}
