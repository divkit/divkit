plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.yandex.divkit.sample"

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    compileSdk = rootProject.ext["compileSdkVersion"] as Int

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    defaultConfig {
        applicationId = "com.yandex.divkit.sample"
        minSdk = 23
        targetSdk = rootProject.ext["targetSdkVersion"] as Int
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
