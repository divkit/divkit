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
    expectedScreenshot: String = "",
    expectedSuite: String = "",
) {
    val (suiteName, caseName) = when {
        screenshotName.isNotEmpty() -> artifactsRelativePath to screenshotName
        stepId != null -> "$artifactsRelativePath/${casePath.caseName}" to "step$stepId"
        else -> artifactsRelativePath to casePath.caseName
    }

    InstrumentationRegistry.getInstrumentation().runOnMainSync {
        val screenshots = ScreenshotCaptor.takeScreenshots(view, suiteName, caseName)
        TestCaseReferencesFileWriter.append(casePath, screenshots)

        if (expectedScreenshot.isEmpty() && expectedSuite.isEmpty()) return@runOnMainSync

        val expected = expectedScreenshot.takeIf { it.isNotEmpty() }
            ?.substringBefore(ScreenshotType.SCREENSHOT_EXTENSION)
            ?: caseName
        if (expected == caseName && expectedSuite.isEmpty()) return@runOnMainSync

        val expectedSuite = expectedSuite.takeIf { it.isNotEmpty() } ?: suiteName
        ScreenshotType.values().forEach {
            ReferenceFileWriter.append(
                targetFile = it.relativeScreenshotPath(suiteName, caseName),
                compareWith = it.relativeScreenshotPath(expectedSuite, expected)
            )
        }
    }
}
