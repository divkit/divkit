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
    api(project(":core"))

    implementation(libs.kotlin.stdlib)

    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.kotlin.test)
}
