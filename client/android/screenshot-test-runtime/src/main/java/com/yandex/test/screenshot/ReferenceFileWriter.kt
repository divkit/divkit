package com.yandex.test.screenshot

import java.io.File

private const val REFERENCE_FILE_NAME = "reference-overrides.json"

/**
 * Keep in sync with `ReferenceFileReader`
 */
class ReferenceFileWriter(fileDir: File) {
    private val referencesFile = File(fileDir, REFERENCE_FILE_NAME)

    fun append(targetFile: String, compareWith: String) {
        referencesFile.appendText("{\"target\":\"$targetFile\", \"compareWith\":\"$compareWith\"}\n")
    }

    fun cleanup() {
        if (referencesFile.exists()) {
            referencesFile.delete()
        }
    }
}
