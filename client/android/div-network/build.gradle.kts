plugins {
    alias(libs.plugins.android.library)
}

apply(from = "../div-library.gradle")
apply(from = "../publish-android.gradle")

android {
    namespace = "com.yandex.div.network"
}

dependencies {
    implementation(project(":div"))

    implementation(libs.okhttp)
}
