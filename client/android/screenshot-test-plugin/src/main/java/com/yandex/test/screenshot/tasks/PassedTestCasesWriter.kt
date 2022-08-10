package com.yandex.test.screenshot.tasks

import java.io.File

private const val PASSED_TEST_CASES_FILE_NAME = "passed_test_cases.json"


class PassedTestCasesWriter(fileDir: File) {
    private val logFile = File(fileDir, PASSED_TEST_CASES_FILE_NAME)

    fun log(passedTestCasePaths: Collection<String>) {
        with(logFile) {
            if (!exists()) {
                createNewFile()
            }
            appendText(passedTestCasePaths.joinToString(prefix = "[", postfix = "]") { "\"$it\"" })
        }
    }
}
