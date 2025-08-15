package com.yandex.test.screenshot

private const val TEST_CASE_REFERENCES_FILE_NAME = "testcase_references.json"

object TestCaseReferencesFileWriter {
    private val referencesFileWriter = TestFile(TEST_CASE_REFERENCES_FILE_NAME).open().writer()

    fun append(caseFilePath: String, screenshotPaths: Collection<String>) {
        referencesFileWriter.write(createJson(caseFilePath, screenshotPaths))
        referencesFileWriter.flush()
    }

    private fun createJson(caseFilePath: String, screenshotPaths: Collection<String>): String {
        val screenshotPathsRaw = screenshotPaths
            .joinToString(prefix = "[", postfix = "]") { "\"$it\"" }
        return "{\"testCasePath\":\"$caseFilePath\", \"screenshotPaths\":$screenshotPathsRaw}\n"
    }
}
