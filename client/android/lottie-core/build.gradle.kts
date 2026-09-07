plugins {
    alias(libs.plugins.android.library)
}

apply(from = "../div-library.gradle")
apply(from = "../div-tests.gradle")
apply(from = "../publish-android.gradle")

android {
    namespace = "com.yandex.div.lottie.core"
}

dependencies {
    api(libs.lottie) {
        exclude(group = "androidx.fragment", module = "fragment")
    }
    implementation(libs.kotlin.corountines.core)

    testImplementation(libs.androidx.test.core)
}
