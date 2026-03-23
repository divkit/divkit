plugins {
    alias(libs.plugins.android.library)
}

apply(from = "../div-library.gradle")

android {
    namespace = "com.yandex.div.test"
}

dependencies {
    implementation(project(":div-data"))
    implementation(project(":div-evaluable"))

    implementation(libs.androidx.core)
    implementation(libs.androidx.coreKtx)
    implementation(libs.junit)
}
