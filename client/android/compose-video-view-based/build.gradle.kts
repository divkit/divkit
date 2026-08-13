plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
}

apply(from = "../div-library.gradle")
apply(from = "../publish-android.gradle")

android {
    namespace = "com.yandex.div.compose.video.viewbased"
}

dependencies {
    implementation(project(":compose"))
    implementation(project(":div"))
    implementation(project(":div-data"))

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.core)
    implementation(libs.kotlin.corountines.core)
}
