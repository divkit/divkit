plugins {
    `kotlin-dsl`
}

kotlin {
    jvmToolchain(libs.versions.jvm.toolchain.get().toInt())
}

dependencies {
    implementation(libs.metalava)
}
