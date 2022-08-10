package com.yandex.test.screenshot.tasks

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.annotations.SerializedName
import java.io.File
import java.io.IOException

private const val REFERENCE_FILE_NAME = "reference-overrides.json"

/**
 * Keep in sync with `ReferenceFileWriter`
 */
class ReferenceFileReader(fileDir: File) {
    private val referencesFile = File(fileDir, REFERENCE_FILE_NAME)
    private val references = mutableMapOf<String, String>()
    private val gson = Gson()

    @Throws(IOException::class)
    fun load() {
        references.clear()
        if (!referencesFile.exists()) {
            return
        }

        referencesFile.readLines().forEach { jsonStruct ->
            val reference = try {
                gson.fromJson(jsonStruct, FileReference::class.java)
            } catch (e: JsonSyntaxException) {
                throw IOException(e)
            }
            references[reference.targetFile] = reference.compareWith
        }
    }

    fun resolveReferencePath(relativePath: String): String? {
        return references[relativePath]
    }

    fun deleteReferenceFile() {
        if (referencesFile.exists()) {
            referencesFile.delete()
        }
    }
}

private data class FileReference(
    @SerializedName("compareWith")
    val compareWith: String,
    @SerializedName("target")
    val targetFile: String,
)
