import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.android.library)
    id("divkit.convention.abi-validation")
}

apply(from = "../div-library.gradle")
apply(from = "../publish-android.gradle")

android {
    namespace = "com.yandex.divkit.svg"
}

dependencies {
    implementation(project(":div-core"))
    implementation(project(":div-network-okhttp"))

    implementation(libs.androidsvg.aar)
    implementation(libs.androidx.core)
    implementation(libs.kotlin.corountines.android)
    implementation(libs.kotlin.corountines.core)
    implementation(libs.okhttp)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.add("-Xexplicit-api=strict")
    }
}
