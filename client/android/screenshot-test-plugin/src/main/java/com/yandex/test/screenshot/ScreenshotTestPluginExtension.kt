package com.yandex.test.screenshot

open class ScreenshotTestPluginExtension {

    var enabled: Boolean = true
    var enableComparison: Boolean = true
    var strictComparison: Boolean = false
    var testAnnotations = mutableListOf<String>()
    var hostDir = "screenshots"
    var referencesDir = "src/androidTest/resources/screenshots"
    var screenshotDir = "outputs/connected_android_test_additional_output/debugAndroidTest/connected"
    val collectedDir get() = "$hostDir/collected"
    val comparisonDir: String
        get() = "$hostDir/comparison"

    val comparableCategories = listOf("viewRender", "viewPixelCopy")

    companion object {
        const val NAME = "screenshotTests"
    }
}
