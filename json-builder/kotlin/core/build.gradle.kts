plugins {
    alias(libs.plugins.kotlin.jvm)
    id("divkit.convention.publishing-module")
}

kotlin {
    jvmToolchain(libs.versions.jvm.toolchain.get().toInt())

    compilerOptions {
        freeCompilerArgs.add("-Xjvm-default=all")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    implementation(libs.kotlin.stdlib)

    implementation(libs.jackson.annotations)
}
