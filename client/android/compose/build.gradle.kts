import com.android.build.api.variant.KotlinMultiplatformAndroidComponentsExtension
import dev.detekt.gradle.Detekt
import java.io.File
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    id("divkit.convention.library-kmp")
    id("divkit.convention.jvm-api-check")
    id("divkit.convention.publishing-module-kmp")
    id("divkit.convention.stub-aar")
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.metro)
    alias(libs.plugins.roborazzi)
}

kotlin {
    // Preserve the existing Android API while moving its source set.
    // TODO(https://st.yandex-team.ru/DIVKIT-11051): Restore strict explicit API mode.
    explicitApi = ExplicitApiMode.Disabled

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    android {
        namespace = "com.yandex.div.compose"
        androidResources.enable = true
        lint {
            // Keep the workaround from div-common.gradle until AGP 9.2+.
            disable.add("WrongConstant")
        }
        withHostTest {
            isIncludeAndroidResources = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project.dependencies.platform(libs.androidx.compose.bom))
            implementation(libs.androidx.compose.ui.text)
        }

        androidMain.dependencies {
            api(project(":core"))
            api(project(":div-data"))

            implementation(project(":coil-core"))
            implementation(project(":div-core"))
            implementation(project(":div-evaluable"))
            implementation(project(":div-storage"))
            implementation(project(":render-common"))

            implementation(libs.androidx.compose.foundation.layout)
            implementation(libs.androidx.compose.material3)
            implementation(libs.androidx.core)
            implementation(libs.coil.compose)
            implementation(libs.coil.network)
        }

        getByName("androidHostTest").dependencies {
            implementation(project(":div-histogram"))
            implementation(project(":fonts"))
            implementation(project(":test-utils"))

            implementation(libs.androidx.test.ext.junit)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.mockito.kotlin)
            implementation(libs.robolectric)
            implementation(libs.androidx.compose.ui.test.junit4)
            implementation(libs.androidx.compose.ui.test.manifest)
            implementation(libs.json)
            implementation(libs.kotlin.test)
            implementation(libs.roborazzi)
            implementation(libs.roborazzi.compose)
            implementation(libs.roborazzi.junit.rule)
            implementation(libs.webp.imageio)

            // SVG is optional for Compose consumers; enable it here to test SVG rendering.
            implementation(libs.coil.svg)
        }
    }
}

extensions.configure<KotlinMultiplatformAndroidComponentsExtension> {
    onVariants { variant ->
        variant.hostTests.values.forEach { test ->
            test.sources.assets?.addStaticSourceDirectory("../test-utils/src/main/res/font")
        }
    }
}

// Android KMP has one variant: preview tooling must not enter published dependencies.
dependencies {
    add("androidRuntimeClasspath", libs.androidx.compose.ui.tooling)
}

val screenshotRun = providers.gradleProperty("roborazzi-tests").isPresent

tasks.withType<Test>().configureEach {
    inputs.dir(rootProject.file("../../test_data"))
        .withPropertyName("divkitTestData")
        .withPathSensitivity(PathSensitivity.RELATIVE)
    inputs.dir(rootProject.file("../../expression-api"))
        .withPropertyName("divkitExpressionApi")
        .withPathSensitivity(PathSensitivity.RELATIVE)
    maxHeapSize = "4g"
    testLogging {
        events("passed", "skipped", "failed", "standardOut", "standardError")
    }
    systemProperty("robolectric.pixelCopyRenderMode", "hardware")
    useJUnit {
        if (screenshotRun) {
            includeCategories("com.yandex.div.test.ScreenshotTests")
        } else {
            excludeCategories("com.yandex.div.test.ScreenshotTests")
        }
    }
    failOnNoDiscoveredTests = screenshotRun
}

// Measurement entry point for ai/scripts/code_complexity: the same engine with
// zeroed thresholds, so the checkstyle report lists every function with its cognitive
// complexity and length. The scorer points it at an export of the revision it measures
// and reads the report back, one invocation per side.
val metricsInput = providers.gradleProperty("detektMetricsInput")
    .getOrElse("src/androidMain/kotlin")
val metricsReport = providers.gradleProperty("detektMetricsReport")
    .getOrElse("build/reports/detekt/metrics.xml")

tasks.register<Detekt>("detektMetrics") {
    group = "verification"
    description = "Reports cognitive complexity and length for every function in " +
        "-PdetektMetricsInput (default src/androidMain/kotlin)."
    buildUponDefaultConfig = true
    config.setFrom(rootProject.file("config/detekt/detekt-metrics.yml"))
    // A baseline would hide exactly the functions this task exists to measure.
    baseline.set(null as File?)
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
