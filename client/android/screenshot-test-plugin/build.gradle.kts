plugins {
    id("java-gradle-plugin")
    alias(libs.plugins.kotlin.jvm)
}

group = "com.yandex.test"
version = "1.0.0"

kotlin {
    jvmToolchain(libs.versions.jvm.toolchain.get().toInt())
}

dependencies {
    implementation(libs.agp.gradle)
    implementation(libs.gson)
    implementation(libs.google.testing.platform.proto)
    implementation(libs.webp.imageio)
}

gradlePlugin {
    plugins {
        create("screenshotTests") {
            id = "com.yandex.test.screenshot-test-plugin"
            implementationClass = "com.yandex.test.screenshot.ScreenshotTestPlugin"
        }
    }
}
