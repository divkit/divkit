package com.yandex.test.screenshot

import android.app.Instrumentation
import androidx.test.platform.app.InstrumentationRegistry
import com.yandex.test.idling.waitForView
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class ScreenshotRule(
    private val relativePath: String,
    private val name: String,
    private val casePath: String
) : TestRule {
    private val instrumentation: Instrumentation
        get() = InstrumentationRegistry.getInstrumentation()

    private var beforeScreenshotTakenAction: (() -> Unit)? = null

    fun beforeScreenshotTaken(action: () -> Unit) {
        beforeScreenshotTakenAction = action
    }

    override fun apply(base: Statement, description: Description): Statement {
        val screenshot = description.getAnnotation(Screenshot::class.java) ?: return base
        val screenshotPath = screenshot.relativePath.ifEmpty { relativePath }
        val screenshotName = screenshot.name.ifEmpty { name.ifEmpty { description.methodName } }
        val suiteName = description.className + "/" + screenshotPath
        val screenshotCapture = ScreenshotCaptor()
        return object : Statement() {
            override fun evaluate() {
                base.evaluate()

                val view = waitForView(screenshot.viewId)
                beforeScreenshotTakenAction?.invoke()

                instrumentation.runOnMainSync {
                    val screenshotRelativePaths =
                        screenshotCapture.takeScreenshots(view, suiteName, screenshotName)

                    TestCaseReferencesFileWriter.append(casePath, screenshotRelativePaths)
                }
            }
        }
    }
}
