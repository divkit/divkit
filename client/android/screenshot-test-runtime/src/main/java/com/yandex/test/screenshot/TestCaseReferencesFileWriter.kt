package com.yandex.test.screenshot

import java.io.File

private const val TEST_CASE_REFERENCES_FILE_NAME = "testcase_references.json"


class TestCaseReferencesFileWriter(fileDir: File) {
    private val referencesFile = File(fileDir, TEST_CASE_REFERENCES_FILE_NAME).apply {
        if (!exists()) {
            createNewFile()
        }
    }

    fun append(caseFilePath: String, screenshotPaths: Collection<String>) {
        referencesFile.appendText(createJson(caseFilePath, screenshotPaths))
    }

    private fun createJson(caseFilePath: String, screenshotPaths: Collection<String>): String {
        val screenshotPathsRaw = screenshotPaths
            .joinToString(prefix = "[", postfix = "]") { "\"$it\"" }
        return "{\"testCasePath\":\"$caseFilePath\", \"screenshotPaths\":$screenshotPathsRaw}\n"
    }
}
