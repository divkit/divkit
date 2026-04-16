plugins {
    alias(libs.plugins.android.library)
}

apply(from = "../div-library.gradle")
apply(from = "../div-tests.gradle")
apply(from = "../publish-android.gradle")

android {
    namespace = "com.yandex.div.lottie"
}

dependencies {
    implementation(project(":assertion"))
    implementation(project(":div"))

    implementation(libs.androidx.appcompat) {
        exclude(group = "androidx.fragment", module = "fragment")
    }
    implementation(libs.kotlin.corountines.core)
    implementation(libs.kotlin.corountines.android)
    implementation(libs.lottie) {
        exclude(group = "com.squareup.okio", module = "okio")
        exclude(group = "androidx.fragment", module = "fragment")
    }
    implementation(libs.okio)
}
