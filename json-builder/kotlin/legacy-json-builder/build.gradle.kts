plugins {
    alias(libs.plugins.kotlin.jvm)
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

    testRuntimeOnly(libs.bundles.junit.runtime)
    testImplementation(libs.bundles.junit.jupiter)
    testImplementation(libs.jsonAssert)
}
