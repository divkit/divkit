import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.android.library)
}

apply(from = "../div-library.gradle")
apply(from = "../publish-android.gradle")

android {
    namespace = "com.yandex.div.video"
}

dependencies {
    implementation(project(":assertion"))
    implementation(project(":div"))
    implementation(project(":logging"))

    implementation(libs.exoplayer)
    implementation(libs.kotlin.corountines.core)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.add("-Xexplicit-api=strict")
    }
}
