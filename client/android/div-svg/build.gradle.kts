import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.android.library)
}

apply(from = "../div-library.gradle")
apply(from = "../div-tests.gradle")
apply(from = "../publish-android.gradle")

android {
    namespace = "com.yandex.divkit.svg"
}

dependencies {
    implementation(project(":div-core"))
    implementation(project(":logging"))

    implementation(libs.androidsvg.aar)
    implementation(libs.androidx.core)
    implementation(libs.androidx.coreKtx)
    implementation(libs.kotlin.corountines.android)
    implementation(libs.kotlin.corountines.core)
    implementation(libs.okhttp)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.add("-Xexplicit-api=strict")
    }
}
