plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.roborazzi)
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
    implementation(libs.coil.gif)
    implementation(libs.coil.network)
    implementation(libs.yatagan.api.compiled)

    ksp(libs.yatagan.processor.ksp)

    debugImplementation(libs.androidx.compose.ui.tooling)

    androidTestImplementation(project(":test-utils"))
    androidTestImplementation(libs.androidx.compose.ui.tooling.preview)

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

    // Adding dependency only to the tests to avoid the apk size increase for clients who do not
    // use svg imgages.
    testImplementation(libs.coil.svg)
}

roborazzi {
    outputDir = file("src/test/screenshots")
}

tasks.withType<Test>().configureEach {
    providers.gradleProperty("divkitTestFilter").orNull?.let { filter ->
        systemProperty("divkit.test.filter", filter)
    }
}
