plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.ksp)
}

apply(from = "../div-library.gradle")
apply(from = "../div-tests.gradle")

android {
    namespace = "com.yandex.div.compose"

    sourceSets {
        getByName("androidTest").kotlin.srcDirs("src/commonTest/kotlin")
        getByName("test").kotlin.srcDirs("src/commonTest/kotlin")
    }
}

dependencies {
    implementation(project(":div-core"))
    implementation(project(":div-data"))
    implementation(project(":div-evaluable"))
    implementation(project(":logging"))

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.coreKtx)
    implementation(libs.coil.compose)
    implementation(libs.coil.network)
    implementation(libs.yatagan.api.compiled)

    ksp(libs.yatagan.processor.ksp)

    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.mockito.android)
    androidTestImplementation(libs.mockito.kotlin)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
