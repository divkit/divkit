plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    id("divkit.convention.abi-validation")
}

apply(from = "../div-library.gradle")
apply(from = "../div-tests.gradle")
apply(from = "../publish-android.gradle")

android {
    namespace = "com.yandex.div.compose.lottie"
}

dependencies {
    api(project(":lottie-core"))
    implementation(project(":compose"))
    implementation(project(":div-core"))
    implementation(project(":div-data"))

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.kotlin.corountines.core)
    implementation(libs.lottie.compose)

    testImplementation(libs.androidx.test.core)
    testImplementation(project(":test-utils"))
    testImplementation(libs.androidx.compose.ui.test.junit4)
    testImplementation(libs.androidx.compose.ui.test.manifest)
}
