package com.yandex.test.rules

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import ru.tinkoff.allure.android.AllureAndroidLifecycle

class WindowHierarchyRule(
    private val reportOnSuccess: Boolean = false
) : TestRule {

    private val instrumentation = InstrumentationRegistry.getInstrumentation()

    private val targetContext: Context
        get() = instrumentation.targetContext

    private val uiDevice: UiDevice
        get() = UiDevice.getInstance(instrumentation)

    override fun apply(base: Statement, description: Description): Statement {
        return object: Statement() {

            override fun evaluate() {
                runCatching {
                    base.evaluate()
                }.onSuccess {
                    if (reportOnSuccess) attachWindowHierarchy()
                }.onFailure { error ->
                    attachWindowHierarchy()
                    throw error
                }
            }
        }
    }

    private fun attachWindowHierarchy() {
        val hierarchyFile = createTempFile(
            prefix = WINDOW_HIERARCHY_FILE_NAME,
            suffix = WINDOW_HIERARCHY_FILE_EXTENSION,
            directory = targetContext.cacheDir
        )
        uiDevice.dumpWindowHierarchy(hierarchyFile)

        AllureAndroidLifecycle.addAttachment(
            name = WINDOW_HIERARCHY_FILE_NAME,
            type = WINDOW_HIERARCHY_MIME_TYPE,
            fileExtension = WINDOW_HIERARCHY_FILE_EXTENSION,
            file = hierarchyFile
        )

        hierarchyFile.delete()
    }

    private companion object {
        private const val WINDOW_HIERARCHY_FILE_NAME = "window-hierarchy"
        private const val WINDOW_HIERARCHY_FILE_EXTENSION = ".xml"
        private const val WINDOW_HIERARCHY_MIME_TYPE = "text/xml"
    }
}
