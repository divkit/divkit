plugins {
    alias(libs.plugins.android.library)
}

apply(from = "../div-library.gradle")
apply(from = "../div-tests.gradle")
apply(from = "../publish-android.gradle")

android {
    namespace = "com.yandex.div.zoom"
}

dependencies {
    implementation(project(":assertion"))
    implementation(project(":div"))
    implementation(project(":logging"))
    implementation(project(":utils"))

    implementation(libs.androidx.appcompat) {
        exclude(group = "androidx.fragment", module = "fragment")
    }
    implementation(libs.androidx.core)
    implementation(libs.androidx.coreKtx)

    testImplementation(libs.androidx.test.core)
}
