plugins {
    alias(libs.plugins.android.library)
}

apply(from = "../div-library.gradle")

android {
    namespace = "com.yandex.test.screenshot"
}

dependencies {
    implementation(project(":ui-test-common"))

    implementation(libs.androidx.test.uiautomator)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
}
