plugins {
    kotlin("jvm")
    id("divkit.convention.publishing-module")
}

kotlin {
    jvmToolchain(17)

    compilerOptions {
        freeCompilerArgs.add("-Xjvm-default=all")
    }
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    compileOnly(libs.detekt.api)

    testRuntimeOnly(libs.bundles.junit.runtime)
    testImplementation(libs.bundles.junit.jupiter)
    testImplementation(libs.assertj.core)
    testImplementation(libs.detekt.test)
}
