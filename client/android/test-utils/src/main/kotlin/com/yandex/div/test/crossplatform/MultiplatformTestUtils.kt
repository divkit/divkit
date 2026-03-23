package com.yandex.div.test.crossplatform

import org.json.JSONException
import java.io.File

private const val JSON_EXTENSION = "json"
private const val TEST_DATA_PATH = "../../../test_data/"

object MultiplatformTestUtils {

    fun walkJSONs(
        relativePath: String,
        parseAction: (file: File, json: String) -> Unit
    ): List<ParsingResult.Error> {
        return walkJSONs(directory = File(TEST_DATA_PATH, relativePath), parseAction)
    }

    fun walkJSONs(
        directory: File,
        parseAction: (file: File, json: String) -> Unit
    ): List<ParsingResult.Error> {
        val errors = mutableListOf<ParsingResult.Error>()
        getFiles(directory)
            .forEach { file ->
                val json = try {
                    file.readText(Charsets.UTF_8)
                } catch (e: Exception) {
                    errors.add(ParsingResult.Error(fileName = file.name, error = e))
                    return@forEach
                }

                try {
                    parseAction(file, json)
                } catch (e: JSONException) {
                    errors.add(ParsingResult.Error(fileName = file.name, error = e))
                }
            }

        return errors
    }

    private fun getFiles(dir: File): List<File> {
        val (directories, files) = dir.listFiles().orEmpty()
            .partition { it.isDirectory }
        return arrayListOf<File>().apply {
            addAll(files.filter { file -> file.extension == JSON_EXTENSION })
            addAll(directories.flatMap { getFiles(it) })
        }
    }
}
