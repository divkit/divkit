import dev.detekt.gradle.Detekt

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
    api(project(":div-data"))

    implementation(project(":coil-core"))
    implementation(project(":div-core"))
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
    implementation(libs.yatagan.api.public)

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

// Measurement entry point for ai/scripts/code_complexity: the same engine with
// zeroed thresholds, so the checkstyle report lists every function with its cognitive
// complexity and length. The scorer points it at an export of the revision it measures
// and reads the report back, one invocation per side.
val metricsInput = providers.gradleProperty("detektMetricsInput")
    .getOrElse("src/main/kotlin")
val metricsReport = providers.gradleProperty("detektMetricsReport")
    .getOrElse("build/reports/detekt/metrics.xml")

tasks.register<Detekt>("detektMetrics") {
    group = "verification"
    description = "Reports cognitive complexity and length for every function in " +
        "-PdetektMetricsInput (default src/main/kotlin)."
    buildUponDefaultConfig = true
    config.setFrom(rootProject.file("config/detekt/detekt-metrics.yml"))
    // A baseline would hide exactly the functions this task exists to measure.
    baseline.set(null as java.io.File?)
    ignoreFailures = true
    val inputDir = projectDir.resolve(metricsInput)
    setSource(inputDir)
    // Report the paths relative to the measured root instead of to this module, so the
    // scorer gets back exactly the paths it exported.
    basePath = inputDir.absolutePath
    reports {
        checkstyle.required = true
        checkstyle.outputLocation.set(projectDir.resolve(metricsReport))
        sarif.required = false
        html.required = false
        markdown.required = false
    }
}

roborazzi {
    outputDir = file("src/test/screenshots")
}

tasks.withType<Test>().configureEach {
    providers.gradleProperty("divkitTestFilter").orNull?.let { filter ->
        systemProperty("divkit.test.filter", filter)
    }
}
