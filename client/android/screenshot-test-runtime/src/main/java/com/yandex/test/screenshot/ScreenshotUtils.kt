package com.yandex.test.screenshot

import android.content.Context
import android.os.Build
import android.view.View
import com.yandex.test.util.performOnMain
import io.qameta.allure.kotlin.Allure
import io.qameta.allure.kotlin.model.Parameter
import java.io.File
import java.util.Properties
import java.util.concurrent.atomic.AtomicBoolean

private val propertiesSaved = AtomicBoolean(false)

val String.caseName: String get() {
    return substringAfterLast(File.separator)
        .substringBeforeLast(".json")
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

    if (!propertiesSaved.getAndSet(true)) {
        saveDeviceProperties(view.context)
    }

    val screenshots = performOnMain {
        ScreenshotCaptor.takeScreenshots(view, suiteName, caseName)
    }
    report(screenshots)

    if (expectedScreenshot.isEmpty() && expectedSuite.isEmpty()) return

    val expected = expectedScreenshot.takeIf { it.isNotEmpty() }
        ?.substringBefore(ScreenshotType.SCREENSHOT_EXTENSION)
        ?: caseName
    if (expected == caseName && expectedSuite.isEmpty() && stepId == null) return

    val expectedSuite = expectedSuite.takeIf { it.isNotEmpty() } ?: suiteName
    ScreenshotType.entries.forEach {
        ReferenceFileWriter.append(
            targetFile = it.relativeScreenshotPath(suiteName, caseName),
            compareWith = it.relativeScreenshotPath(expectedSuite, expected)
        )
    }
}

private fun saveDeviceProperties(context: Context) {
    val specs = DeviceSpecs(context)
    val properties = Properties().apply {
        put("apiLevel", Build.VERSION.SDK_INT.toString())
        put("displayWidth", specs.displayWidth.toString())
        put("displayHeight", specs.displayHeight.toString())
        put("displayDensity", specs.density.toString())
    }

    TestFile("device.properties").open().bufferedWriter().use {
        properties.store(it, null)
    }
}

private fun report(screenshots: Collection<String>) {
    val lifecycle = Allure.lifecycle
    val resultId = lifecycle.getCurrentTestCaseOrStep() ?: return
    val parameters = screenshots.map { Parameter(name = "Screenshot", value = it) }
    if (resultId == lifecycle.getCurrentTestCase()) {
        lifecycle.updateTestCase(resultId) { it.parameters.addAll(parameters) }
    } else {
        lifecycle.updateStep(resultId) { it.parameters.addAll(parameters) }
    }
}
