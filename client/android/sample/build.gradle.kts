plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.yandex.divkit.sample"

    buildFeatures {
        viewBinding = true
    }

    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.yandex.divkit.sample"
        minSdk = 23
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0.0"
    }
}

dependencies {
    implementation(project(":coil"))
    implementation(project(":div"))
    implementation(project(":div-markdown"))
    implementation(project(":div-pinch-to-zoom"))
    implementation(project(":div-rive"))
    implementation(project(":utils"))

    implementation(libs.androidx.activity)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.coreKtx)
    implementation(libs.androidx.material)
    implementation(libs.okhttp)
}
