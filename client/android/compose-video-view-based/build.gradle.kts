plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    id("divkit.convention.abi-validation")
}

apply(from = "../div-library.gradle")
apply(from = "../div-tests.gradle")
apply(from = "../publish-android.gradle")

android {
    namespace = "com.yandex.div.compose.video.viewbased"
}

dependencies {
    implementation(project(":compose"))
    implementation(project(":div"))

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.core)
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(project(":test-utils"))
    testImplementation(libs.androidx.compose.ui.test.junit4)
    testImplementation(libs.androidx.compose.ui.test.manifest)
    testImplementation(libs.json)
}
