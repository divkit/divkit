package com.yandex.test.rules

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.yandex.test.util.DeviceShellManager
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import ru.tinkoff.allure.android.AllureAndroidLifecycle
import java.io.File

class LogcatReportRule(
    private val reportOnSuccess: Boolean = false
) : TestRule {

    private val targetContext: Context
        get() = InstrumentationRegistry.getInstrumentation().targetContext

    override fun apply(base: Statement, description: Description): Statement {
        return SimpleStatement {
            runCatching {
                clearLogcat()
                base.evaluate()
            }.onSuccess {
                if (reportOnSuccess) attachLogcatToReport()
            }.onFailure { error ->
                attachLogcatToReport()
                throw error
            }
        }
    }

    private fun attachLogcatToReport() {
        val logcatFile = File.createTempFile(
            LOG_CAT_REPORT_FILE_NAME,
            LOG_CAT_REPORT_FILE_EXTENSION,
            targetContext.cacheDir
        )
        dumpLogcat(logcatFile)

        AllureAndroidLifecycle.addAttachment(
            name = LOG_CAT_REPORT_FILE_NAME,
            type = LOG_CAT_REPORT_FILE_MIME_TYPE,
            fileExtension = LOG_CAT_REPORT_FILE_EXTENSION,
            file = logcatFile
        )

        logcatFile.delete()
    }

    private fun clearLogcat() {
        val shellCommand = "logcat -b all -c"
        DeviceShellManager.executeDeviceShellCommand(shellCommand)
    }

    private fun dumpLogcat(logcatFile: File) {
        DeviceShellManager.run {
            val shellCommand =
                "run-as ${targetContext.packageName} logcat -d -f ${shellEscape(logcatFile.absolutePath)}"
            executeDeviceShellCommand(shellCommand)
        }
    }

    private companion object {
        private const val LOG_CAT_REPORT_FILE_NAME = "logcat"
        private const val LOG_CAT_REPORT_FILE_MIME_TYPE = "text/plain"
        private const val LOG_CAT_REPORT_FILE_EXTENSION = ".txt"
    }
}
