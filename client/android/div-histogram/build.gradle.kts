plugins {
    alias(libs.plugins.android.library)
}

apply(from = "../div-library.gradle")
apply(from = "../div-tests.gradle")
apply(from = "../publish-android.gradle")

android {
    namespace = "com.yandex.div.histogram"
}

dependencies {
    implementation(project(":assertion"))
    implementation(project(":div-core"))
    implementation(project(":logging"))
    implementation(project(":utils"))

    implementation(libs.androidx.coreKtx)
    implementation(libs.yatagan.api.compiled)
}
