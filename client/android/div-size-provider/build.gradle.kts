plugins {
    alias(libs.plugins.android.library)
}

apply(from = "../div-library.gradle")
apply(from = "../div-tests.gradle")
apply(from = "../publish-android.gradle")

android {
    namespace = "com.yandex.div.sizeprovider"
}

dependencies {
    implementation(project(":div"))
    implementation(project(":utils"))

    testImplementation(libs.androidx.test.coreKtx)
}
