plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(17)

    compilerOptions {
        freeCompilerArgs.add("-Xjvm-default=all")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    api(project(":core"))

    implementation(project(":expression-dsl"))

    implementation(libs.kotlin.stdlib)
}
