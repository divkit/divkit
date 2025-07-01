package com.yandex.div.test.expression

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File

private const val PLATFORM_FIELD = "platforms"
private const val VALUE_PLATFORM_ANDROID = "android"
private const val JSON_EXTENSION = "json"
private const val DIV2_JSON_PATH = "../../../test_data/"

object MultiplatformTestUtils {
    fun walkJSONs(
        relativePath: String,
        parseAction: (file: File, json: JSONObject) -> Unit
    ): List<TestCaseParsingError> {
        val errors = mutableListOf<TestCaseParsingError>()
        getFiles(File(DIV2_JSON_PATH, relativePath))
            .forEach { file ->
                val json = try {
                    JSONObject(file.readText(Charsets.UTF_8))
                } catch (e: Exception) {
                    errors.add(
                        TestCaseParsingError(fileName = file.name, json = null, error = e)
                    )
                    return@forEach
                }

                try {
                    parseAction(file, json)
                } catch (e: JSONException) {
                    errors.add(
                        TestCaseParsingError(fileName = file.name, json = null, error = e)
                    )
                }
            }

        return errors
    }

    fun parsePlatform(json: JSONObject): List<String> {
        return json.getJSONArray(PLATFORM_FIELD).toListOfString()
    }

    fun isForAndroidPlatform(platform: List<String>?): Boolean {
        return platform?.contains(VALUE_PLATFORM_ANDROID) == true
    }

    fun JSONArray?.toListOfJSONObject(): List<JSONObject> {
        if (this == null) {
            return emptyList()
        }
        val result = mutableListOf<JSONObject>()
        for (i in 0 until this.length()) {
            result.add(this.getJSONObject(i))
        }
        return result
    }

    private fun getFiles(dir: File): List<File> {
        val (directories, files) = dir.listFiles().orEmpty()
            .partition { it.isDirectory }
        return arrayListOf<File>().apply {
            addAll(files.filter { file -> file.extension == JSON_EXTENSION })
            addAll(directories.flatMap { getFiles(it) })
        }
    }

    private fun JSONArray.toListOfString(): List<String> {
        val result = mutableListOf<String>()
        for (i in 0 until this.length()) {
            result.add(this.getString(i))
        }
        return result
    }
}
