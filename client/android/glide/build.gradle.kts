plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.ksp)
}

apply(from = "../div-library.gradle")
apply(from = "../div-tests.gradle")
apply(from = "../publish-android.gradle")

android {
    namespace = "com.yandex.divkit.glide"
}

dependencies {
    implementation(project(":div"))
    implementation(project(":div-core"))

    implementation(libs.glide.core) {
        exclude(group = "androidx.fragment", module = "fragment")
    }

    ksp(libs.glide.ksp)
}
