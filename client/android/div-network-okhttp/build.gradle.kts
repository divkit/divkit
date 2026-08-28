plugins {
    alias(libs.plugins.android.library)
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
