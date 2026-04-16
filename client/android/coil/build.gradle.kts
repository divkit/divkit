plugins {
    alias(libs.plugins.android.library)
}

apply(from = "../div-library.gradle")
apply(from = "../div-tests.gradle")
apply(from = "../publish-android.gradle")

android {
    namespace = "com.yandex.divkit.coil"
}

dependencies {
    implementation(project(":div"))
    implementation(project(":div-core"))

    api(libs.coil)
    api(libs.coil.svg)

    implementation(libs.coil.gif)
    implementation(libs.coil.network)
    implementation(libs.coil.network.cachecontrol)
}
