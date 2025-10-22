plugins {
    alias(libs.plugins.kotlin.jvm)
    id("divkit.convention.publishing-module")
}

kotlin {
    jvmToolchain(17)

    compilerOptions {
        freeCompilerArgs.addAll(
            "-Xjvm-default=all",
            "-Xannotation-default-target=param-property"
        )
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
    api(project(":core"))
    api(project(":expression-dsl"))

    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)

    implementation(libs.jackson.annotations)
    implementation(libs.jackson.databind)
    implementation(libs.jackson.kotlin)

    testRuntimeOnly(libs.bundles.junit.runtime)
    testImplementation(libs.bundles.junit.jupiter)
    testImplementation(libs.jsonAssert)
}
