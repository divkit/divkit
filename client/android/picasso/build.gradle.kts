plugins {
    alias(libs.plugins.android.library)
}

apply(from = "../div-library.gradle")
apply(from = "../div-tests.gradle")

android {
    namespace = "com.yandex.divkit.picasso"
}

dependencies {
    implementation(project(":div"))
    implementation(project(":div-core"))
    implementation(project(":div-svg"))
    implementation(project(":utils"))

    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.kotlin.corountines.core)
    implementation(libs.okhttp)
    implementation(libs.picasso)
}
