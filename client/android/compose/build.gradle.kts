plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.ksp)
}

apply(from = "../div-library.gradle")
apply(from = "../div-tests.gradle")

android {
    namespace = "com.yandex.div.compose"

    defaultConfig {
        minSdk = 23
    }
}

dependencies {
    implementation(project(":div-core"))
    implementation(project(":div-data"))
    implementation(project(":div-evaluable"))
    implementation(project(":logging"))

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.coreKtx)
    implementation(libs.coil.compose)
    implementation(libs.coil.network)
    implementation(libs.yatagan.api.compiled)

    ksp(libs.yatagan.processor.ksp)

    debugImplementation(libs.androidx.compose.ui.tooling)

    androidTestImplementation(project(":test-utils"))
    androidTestImplementation(libs.androidx.compose.ui.tooling.preview)

    testImplementation(project(":test-utils"))
    testImplementation(libs.androidx.compose.ui.test.junit4)
    testImplementation(libs.androidx.compose.ui.test.manifest)
}
