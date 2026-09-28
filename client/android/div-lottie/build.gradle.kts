plugins {
    alias(libs.plugins.android.library)
    id("divkit.convention.abi-validation")
}

apply(from = "../div-library.gradle")
apply(from = "../div-tests.gradle")
apply(from = "../publish-android.gradle")

android {
    namespace = "com.yandex.div.lottie"
}

dependencies {
    api(project(":lottie-core"))
    implementation(project(":div"))

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.lottie) {
        exclude(group = "androidx.fragment", module = "fragment")
    }

    testImplementation(libs.androidx.test.core)
    testImplementation(libs.okhttp)
    testImplementation(project(":div-network-okhttp"))
}
