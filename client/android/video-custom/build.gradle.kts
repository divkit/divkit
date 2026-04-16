plugins {
    alias(libs.plugins.android.library)
}

apply(from = "../div-library.gradle")
apply(from = "../div-tests.gradle")
apply(from = "../publish-android.gradle")

android {
    namespace = "com.yandex.div.video.custom"
}

dependencies {
    implementation(project(":assertion"))
    implementation(project(":div"))
    implementation(project(":logging"))

    implementation(libs.androidx.coreKtx)
    implementation(libs.exoplayer)
    implementation(libs.javax)
    implementation(libs.kotlin.corountines.core)
}
