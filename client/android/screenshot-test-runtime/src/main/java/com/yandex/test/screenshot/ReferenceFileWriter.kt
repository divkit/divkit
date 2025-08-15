package com.yandex.test.screenshot

private const val REFERENCE_FILE_NAME = "reference-overrides.json"

/**
 * Keep in sync with `ReferenceFileReader`
 */
object ReferenceFileWriter {
    private val referencesFileWriter = TestFile(REFERENCE_FILE_NAME).open().writer()

    fun append(targetFile: String, compareWith: String) {
        referencesFileWriter.write("{\"target\":\"$targetFile\", \"compareWith\":\"$compareWith\"}\n")
        referencesFileWriter.flush()
    }
}
