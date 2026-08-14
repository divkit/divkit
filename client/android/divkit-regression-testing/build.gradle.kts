plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.ksp)
}

apply(from = "../div-library.gradle")
apply(from = "../div-tests.gradle")

android {
    namespace = "com.yandex.divkit.regression"

    sourceSets {
        getByName("main") {
            assets.srcDirs("../../test_data/regression_test_data")
        }
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    buildTypes {
        release {
            consumerProguardFiles("proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(project(":div"))
    implementation(project(":fonts"))

    implementation(libs.androidx.activity)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.material)
    implementation(libs.androidx.recyclerview)
    implementation(libs.gson)
    implementation(libs.yatagan.api.compiled)

    ksp(libs.yatagan.processor.ksp)
}
