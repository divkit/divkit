plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.cocoapods)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)

    id("divkit.convention.android-publishing-module")
}

kotlin {
    androidTarget {
        publishLibraryVariants("release")
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "divkit-multiplatform"
            isStatic = true
        }
    }

    cocoapods {
        version = "1.0"
        ios.deploymentTarget = "13.0"

        framework {
            baseName = "divkit-multiplatform"
        }

        pod("DivKitKMP") {
            source = path(project.file("../divkit-multiplatform/DivKitKMP"))
            extraOpts += listOf("-compiler-option", "-fmodules")
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.ui)
            implementation(compose.foundation)
        }

        androidMain.dependencies {
            implementation(compose.uiTooling)
            implementation(libs.androidx.activityCompose)
            implementation(libs.div)
            implementation(libs.div.core)
            implementation(libs.div.picasso)
            implementation(libs.div.shimmer)
        }
    }
}

android {
    namespace = "com.yandex.divkit.multiplatform"
    compileSdk = 36

    defaultConfig {
        minSdk = 21
    }
}
