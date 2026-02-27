plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
}

apply(from = "../div-library.gradle")

android {
    namespace = "com.yandex.div.compose"
}

dependencies {
    implementation(project(":div-data"))

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.coreKtx)
    implementation(libs.coil.compose)
    implementation(libs.coil.network)

    debugImplementation(libs.androidx.compose.ui.tooling)
}
