plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    id("divkit.convention.abi-validation")
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
    implementation(project(":div-svg"))

    implementation(libs.glide.core) {
        exclude(group = "androidx.fragment", module = "fragment")
    }

    ksp(libs.glide.ksp)

    testRuntimeOnly(libs.androidx.fragment)
}
