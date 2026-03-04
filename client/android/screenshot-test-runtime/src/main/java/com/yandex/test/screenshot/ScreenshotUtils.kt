package com.yandex.test.screenshot

import android.view.View
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File

const val DIV_SCREENSHOT_CASE_EXTENSION = ".json"

val String.caseName: String get() {
    return substringAfterLast(File.separator)
        .substringBeforeLast(DIV_SCREENSHOT_CASE_EXTENSION)
}

fun captureScreenshots(
    view: View,
    artifactsRelativePath: String,
    casePath: String,
    screenshotName: String = "",
    stepId: Int? = null,
    expectedScreenshot: String = ""
) {
    val (suiteName, caseName) = when {
        screenshotName.isNotEmpty() -> artifactsRelativePath to screenshotName
        stepId != null -> "$artifactsRelativePath/${casePath.caseName}" to "step$stepId"
        else -> artifactsRelativePath to casePath.caseName
    }

    InstrumentationRegistry.getInstrumentation().runOnMainSync {
        val screenshots = ScreenshotCaptor.takeScreenshots(view, suiteName, caseName)
        TestCaseReferencesFileWriter.append(casePath, screenshots)

        if (expectedScreenshot.isEmpty()) return@runOnMainSync

        val expected = expectedScreenshot.substringBefore(ScreenshotType.SCREENSHOT_EXTENSION)
        if (expected == caseName) return@runOnMainSync

        appendReference(ScreenshotType.ViewRender, suiteName, caseName, expected)
        appendReference(ScreenshotType.ViewPixelCopy, suiteName, caseName, expected)
    }
}

private fun appendReference(type: ScreenshotType, suiteName: String, actual: String, expected: String) {
    ReferenceFileWriter.append(
        targetFile = type.relativeScreenshotPath(suiteName, actual),
        compareWith = type.relativeScreenshotPath(suiteName, expected)
    )
}
