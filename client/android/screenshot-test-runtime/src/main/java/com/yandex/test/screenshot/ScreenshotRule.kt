package com.yandex.test.screenshot

import com.yandex.test.idling.waitForView
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class ScreenshotRule(
    private val casePath: String,
    private val relativePath: String,
    private val expectedSuite: String,
) : TestRule {

    private var beforeScreenshotTakenAction: (() -> Unit)? = null

    fun beforeScreenshotTaken(action: () -> Unit) {
        beforeScreenshotTakenAction = action
    }

    override fun apply(base: Statement, description: Description): Statement {
        val screenshot = description.getAnnotation(Screenshot::class.java) ?: return base
        val screenshotPath = screenshot.relativePath.ifEmpty { relativePath }
        val suiteName = description.className + "/" + screenshotPath
        val expectedSuite = if (expectedSuite.isNotEmpty()) "$expectedSuite/$screenshotPath" else ""
        return object : Statement() {
            override fun evaluate() {
                base.evaluate()

                val view = waitForView(screenshot.viewId)
                beforeScreenshotTakenAction?.invoke()

                captureScreenshots(view, suiteName, casePath, screenshot.name, expectedSuite = expectedSuite)
            }
        }
    }
}
