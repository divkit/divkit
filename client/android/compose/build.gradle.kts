plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.roborazzi)
    id("divkit.convention.abi-validation")
}

apply(from = "../div-library.gradle")
apply(from = "../div-tests.gradle")
apply(from = "../publish-android.gradle")

android {
    namespace = "com.yandex.div.compose"

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            all {
                it.systemProperties["robolectric.pixelCopyRenderMode"] = "hardware"
            }
        }
    }
}

dependencies {
    implementation(project(":coil-core"))
    implementation(project(":div-core"))
    implementation(project(":div-data"))
    implementation(project(":div-evaluable"))
    implementation(project(":div-histogram"))
    implementation(project(":div-storage"))
    implementation(project(":logging"))

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.core)
    implementation(libs.coil.compose)
    implementation(libs.coil.network)
    implementation(libs.yatagan.api.compiled)

    ksp(libs.yatagan.processor.ksp)

    debugImplementation(libs.androidx.compose.ui.tooling)

    testImplementation(project(":fonts"))
    testImplementation(project(":test-utils"))

    testImplementation(libs.androidx.compose.ui.test.junit4)
    testImplementation(libs.androidx.compose.ui.test.manifest)
    testImplementation(libs.json)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.roborazzi)
    testImplementation(libs.roborazzi.compose)
    testImplementation(libs.roborazzi.junit.rule)
    testImplementation(libs.webp.imageio)

    // SVG is optional for Compose consumers; enable it here to test SVG rendering.
    testImplementation(libs.coil.svg)
}

roborazzi {
    outputDir = file("src/test/screenshots")
}

tasks.withType<Test>().configureEach {
    providers.gradleProperty("divkitTestFilter").orNull?.let { filter ->
        systemProperty("divkit.test.filter", filter)
    }

    // Measured to be faster than both the serial default (forks=1) and full parallelism
    // (forks=availableProcessors()) for this module's Robolectric test suite: running every
    // fork at once causes per-fork JVM/Robolectric startup to compete for the same CPU and
    // heap, which outweighs the parallelism gain.
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)
}
