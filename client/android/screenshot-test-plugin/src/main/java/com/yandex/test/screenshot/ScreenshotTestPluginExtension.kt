package com.yandex.test.screenshot

open class ScreenshotTestPluginExtension {

    var enabled: Boolean = true
    var enableComparison: Boolean = true
    var strictComparison: Boolean = false
    var testAnnotations = mutableListOf<String>()
    var deviceDir = "screenshots"
    var hostDir = "screenshots"
    var referencesDir = "src/androidTest/resources/screenshots"

    val collectedDir: String
        get() = "$hostDir/collected"

    val comparisonDir: String
        get() = "$hostDir/comparison"

    val comparableCategories = listOf("viewRender", "viewPixelCopy")

    companion object {
        const val NAME = "screenshotTests"
    }
}
