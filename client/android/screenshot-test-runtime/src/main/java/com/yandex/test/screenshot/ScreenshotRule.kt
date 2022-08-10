package com.yandex.test.screenshot

import android.app.Instrumentation
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.yandex.test.idling.waitForView
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class ScreenshotRule(
    private val relativePath: String,
    private val name: String,
    private val casePath: String? = null
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
        val suiteDirs = SuiteDirs(description.className + "/" + screenshotPath)
        val screenshotCapture = ScreenshotCaptor()
        val testCaseReferenceFile = TestCaseReferencesFileWriter(
            fileDir = screenshotCapture.screenshotRootDir
        )

        return object : Statement() {
            override fun evaluate() {
                base.evaluate()

                val view = waitForView(screenshot.viewId)
                beforeScreenshotTakenAction?.invoke()

                instrumentation.runOnMainSync {
                    val instrumentation = InstrumentationRegistry.getInstrumentation()
                    val device = UiDevice.getInstance(instrumentation)

                    val screenshotRelativePaths =
                        screenshotCapture.takeScreenshots(device, view, suiteDirs, screenshotName)
                    casePath?.let { casePath ->
                        testCaseReferenceFile.append(casePath, screenshotRelativePaths)
                    }
                }
            }
        }
    }
}
