plugins {
    alias(libs.plugins.android.library)
}

apply(from = "../div-library.gradle")
apply(from = "../div-tests.gradle")
apply(from = "../publish-android.gradle")

android {
    namespace = "com.yandex.div.font.typeface"
}

dependencies {
    implementation(project(":div-core"))

    implementation(libs.androidx.appcompat) {
        exclude(group = "androidx.fragment", module = "fragment")
    }
    implementation(libs.androidx.core)
    implementation(libs.javax)
}
