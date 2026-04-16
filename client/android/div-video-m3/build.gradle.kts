plugins {
    alias(libs.plugins.android.library)
}

apply(from = "../div-library.gradle")
apply(from = "../publish-android.gradle")

android {
    namespace = "com.yandex.div.video.m3"
}

dependencies {
    implementation(project(":assertion"))
    implementation(project(":div"))
    implementation(project(":logging"))

    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.exoplayer.dash)
    implementation(libs.androidx.media3.exoplayer.hls)
    implementation(libs.androidx.media3.exoplayer.rtsp)
    implementation(libs.androidx.media3.exoplayer.smoothstreaming)
    implementation(libs.androidx.media3.ui)
    implementation(libs.kotlin.corountines.core)
}
