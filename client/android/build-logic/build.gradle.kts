plugins {
    `kotlin-dsl`
}

kotlin {
    jvmToolchain(libs.versions.jvm.toolchain.get().toInt())
}

dependencies {
    implementation(libs.agp.gradle)
    implementation(libs.kotlin.gradle)
    implementation(libs.metalava)
}
