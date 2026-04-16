import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.android.library)
}

apply(from = "../div-library.gradle")
apply(from = "../div-tests.gradle")
apply(from = "../publish-android.gradle")

android {
    namespace = "com.yandex.div.util"
}

dependencies {
    implementation(project(":assertion"))
    implementation(project(":div-core"))

    implementation(libs.androidx.annotations)
    implementation(libs.androidx.appcompat) {
        exclude(group = "androidx.fragment", module = "fragment")
    }
    implementation(libs.androidx.collection)
    implementation(libs.androidx.core)
    implementation(libs.androidx.viewpager)
    implementation(libs.javax)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.add("-Xexplicit-api=strict")
    }
}
