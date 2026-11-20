plugins {
    alias(libs.plugins.android.library)
    id("divkit.convention.abi-validation")
}

apply(from = "../div-library.gradle")
apply(from = "../publish-android.gradle")

android {
    namespace = "com.yandex.divkit.coil"
}

dependencies {
    implementation(project(":coil-core"))
    implementation(project(":div"))
    implementation(project(":div-core"))

    api(libs.coil)

    implementation(libs.coil.network.cachecontrol)
    implementation(libs.coil.network.okhttp)
    implementation(libs.coil.svg)
}
