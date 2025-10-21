plugins {
    alias(libs.plugins.kotlin.jvm)
    id("divkit.convention.publishing-module")
}

kotlin {
    jvmToolchain(17)

    compilerOptions {
        freeCompilerArgs.add("-Xjvm-default=all")
    }
}

sourceSets {
    main {
        java {
            srcDir("src/generated/kotlin")
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)

    implementation(libs.jackson.annotations)
    implementation(libs.jackson.databind)
    implementation(libs.jackson.kotlin)

    testImplementation(kotlin("test"))
    testImplementation(libs.jsonAssert)
}
