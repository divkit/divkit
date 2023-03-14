package com.yandex.test.rules

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import ru.tinkoff.allure.android.AllureAndroidLifecycle
import java.io.File
import java.util.concurrent.TimeUnit

private const val FILE_NAME = "failshot"
private const val FILE_TYPE = "image/png"
private const val FILE_EXTENSION = ".png"

class FailshotRule : TestRule {

    private val instrumentation = InstrumentationRegistry.getInstrumentation()

    private val cacheDir get() = instrumentation.targetContext.cacheDir

    private val uiDevice get() = UiDevice.getInstance(instrumentation)

    override fun apply(base: Statement, description: Description) = object : Statement() {
        override fun evaluate() {
            runCatching {
                base.evaluate()
            }.onFailure { error ->
                failshot()
                throw error
            }
        }
    }

    private fun failshot() {
        val file = File.createTempFile(FILE_NAME, FILE_TYPE, cacheDir)
        uiDevice.waitForIdle(TimeUnit.SECONDS.toMillis(5L))
        uiDevice.takeScreenshot(file)
        AllureAndroidLifecycle.addAttachment(FILE_NAME, FILE_TYPE, FILE_EXTENSION, file)
    }
}
