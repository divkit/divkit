plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
}

apply(from = "../div-library.gradle")
apply(from = "../publish-android.gradle")

android {
    namespace = "com.yandex.div.compose.video.viewbased"

    defaultConfig {
        minSdk = 23
    }
}

dependencies {
    implementation(project(":compose"))
    implementation(project(":div"))
    implementation(project(":div-data"))

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.coreKtx)
}
