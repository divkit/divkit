import com.yandex.divkit.gradle.Version

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
    alias(libs.plugins.kotlin.multiplatform)

    id("divkit.convention.version")
}

val divkitVersion: Version by rootProject.extra

kotlin {
    androidTarget {}

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "SampleApp"
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
    namespace = "com.yandex.divkit.multiplaform.sample"
    compileSdk = 36

    defaultConfig {
        minSdk = 21
        targetSdk = 35

        applicationId = "com.yandex.divkit.multiplaform.sample"
        versionCode = divkitVersion.versionCode
        versionName = divkitVersion.versionName
    }
}
