plugins {
    alias(libs.plugins.android.library)
}

apply(from = "../div-library.gradle")

android {
    namespace = "com.yandex.test"
}

dependencies {
    implementation(project(":div-core"))

    api(libs.androidx.appcompat)
    api(libs.androidx.core)
    api(libs.androidx.coreKtx)
    api(libs.androidx.espresso.core)
    api(libs.androidx.espresso.contrib) {
        exclude(group = "org.checkerframework", module = "checker-qual")
    }
    api(libs.androidx.espresso.intents)
    api(libs.androidx.test.core)
    api(libs.androidx.test.runner)
    api(libs.androidx.test.rules)

    implementation(libs.androidx.test.uiautomator)
    implementation(libs.fest.reflect)
}
