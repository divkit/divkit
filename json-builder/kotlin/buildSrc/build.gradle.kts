plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

kotlin {
    jvmToolchain(libs.versions.jvm.toolchain.get().toInt())
}

dependencies {
    implementation(libs.nexusPublish)
}
