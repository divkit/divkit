import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(libs.versions.jvm.toolchain.get().toInt())
}

dependencies {
    compileOnly(libs.android.lint.api)

    testImplementation(libs.junit)
    testImplementation(libs.android.lint)
    testImplementation(libs.android.lint.tests)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        languageVersion = KotlinVersion.KOTLIN_2_2
        apiVersion = KotlinVersion.KOTLIN_2_2
        jvmTarget = JvmTarget.fromTarget(libs.versions.jvm.toolchain.get())
    }
}
