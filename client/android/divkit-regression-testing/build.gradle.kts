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
}

dependencies {
    implementation(project(":assertion"))
    implementation(project(":div"))
    implementation(project(":fonts"))
    implementation(project(":logging"))
    implementation(project(":utils"))

    api(libs.yatagan.api.compiled)
    ksp(libs.yatagan.processor.ksp)

    implementation(libs.androidx.activity)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core)
    implementation(libs.androidx.coreKtx)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.lifecycle.common.java8)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.material)
    implementation(libs.androidx.recyclerview)
    implementation(libs.gson)

    testImplementation(libs.kotlin.corountines.test)
}
