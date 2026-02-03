plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.cocoapods)
    alias(libs.plugins.kotlin.multiplatform)

    id("divkit.convention.android-publishing-module")
}

kotlin {
    androidLibrary {
        namespace = "com.yandex.divkit.multiplatform"
        compileSdk = 36
        minSdk = 23
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
            implementation(libs.compose.foundation)
            implementation(libs.compose.runtime)
            implementation(libs.compose.ui)
        }

        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.compose.ui.tooling)
            implementation(libs.div)
            implementation(libs.div.core)
            implementation(libs.div.picasso)
            implementation(libs.div.shimmer)
        }
    }
}
