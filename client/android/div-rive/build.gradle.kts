plugins {
    alias(libs.plugins.android.library)
}

apply(from = "../div-library.gradle")
apply(from = "../div-tests.gradle")
apply(from = "../publish-android.gradle")

android {
    namespace = "com.yandex.div.rive"
}

dependencies {
    implementation(project(":div"))

    implementation(libs.androidx.appcompat) {
        exclude(group = "androidx.fragment", module = "fragment")
    }
    implementation(libs.rive) {
        exclude(group = "androidx.fragment", module = "fragment")
    }

    compileOnly(libs.okhttp)

    testImplementation(libs.json)
}
