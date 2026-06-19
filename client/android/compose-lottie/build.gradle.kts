plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
}

apply(from = "../div-library.gradle")
apply(from = "../publish-android.gradle")

android {
    namespace = "com.yandex.div.compose.lottie"

    defaultConfig {
        minSdk = 23
    }
}

dependencies {
    implementation(project(":compose"))
    implementation(project(":div-core"))
    implementation(project(":div-data"))

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.kotlin.corountines.core)
    implementation(libs.lottie.compose)
}
