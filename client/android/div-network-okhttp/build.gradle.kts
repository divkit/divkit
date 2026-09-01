plugins {
    alias(libs.plugins.android.library)
    id("divkit.convention.abi-validation")
}

apply(from = "../div-library.gradle")
apply(from = "../publish-android.gradle")

android {
    namespace = "com.yandex.div.network.okhttp"
}

dependencies {
    implementation(project(":div-core"))

    implementation(libs.kotlin.corountines.core)
    implementation(libs.okhttp)

    testImplementation(libs.kotlin.test)
    testImplementation(libs.mockito.kotlin)
}
