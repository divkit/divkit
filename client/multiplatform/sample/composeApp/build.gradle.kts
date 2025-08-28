plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    androidTarget {}

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.ui)
            implementation(compose.foundation)
            implementation(compose.material3)

            implementation(project(":divkit-multiplatform"))
        }

        androidMain.dependencies {
            implementation(libs.androidx.activityCompose)
            implementation(libs.div)
            implementation(libs.div.picasso)
            implementation(libs.div.glide)
        }
    }
}

android {
    namespace = "com.yandex.divkit.compose"
    compileSdk = 36

    defaultConfig {
        minSdk = 21
        targetSdk = 35

        applicationId = "com.yandex.divkit.compose.androidApp"
        versionCode = 1
        versionName = "1.0.0"
    }
}
