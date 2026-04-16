plugins {
    alias(libs.plugins.android.library)
}

apply(from = "../div-library.gradle")
apply(from = "../div-tests.gradle")
apply(from = "../publish-android.gradle")

android {
    namespace = "com.yandex.div.storage"
}

dependencies {
    implementation(project(":assertion"))
    implementation(project(":div-core"))
    implementation(project(":div-data"))
    implementation(project(":div-histogram"))
    implementation(project(":logging"))
    implementation(project(":utils"))

    implementation(libs.androidx.appcompat) {
        exclude(group = "androidx.fragment", module = "fragment")
    }
    implementation(libs.androidx.coreKtx)
    implementation(libs.javax)

    testImplementation(libs.androidx.test.core)
    testImplementation(libs.kotlin.corountines.core)
}
