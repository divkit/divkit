plugins {
    alias(libs.plugins.android.library)
    id("divkit.convention.abi-validation")
}

apply(from = "../div-library.gradle")
apply(from = "../publish-android.gradle")

android {
    namespace = "com.yandex.div.coil.core"

    defaultConfig {
        consumerProguardFiles("proguard-rules.pro")
    }
}

dependencies {
    implementation(project(":div-core"))
    implementation(libs.coil.core)
    implementation(libs.coil.gif)

    compileOnly(libs.androidsvg.aar)
    compileOnly(libs.coil.svg)
}
