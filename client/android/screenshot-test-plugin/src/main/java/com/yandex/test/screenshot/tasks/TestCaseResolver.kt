package com.yandex.test.screenshot.tasks

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.annotations.SerializedName
import java.io.File
import java.io.IOException

private const val TEST_CASE_REFERENCES_FILE_NAME = "testcase_references.json"


class TestCaseResolver(fileDir: File) {
    private val caseReferencesFile = File(fileDir, TEST_CASE_REFERENCES_FILE_NAME)
    // Map of screenshot path to test case path
    private val caseReferences = hashMapOf<String, String>()

    init {
        load()
    }


    fun getTaskName(screenshotPath: String): String? {
        return caseReferences[screenshotPath]
    }

    @Throws(IOException::class)
    private fun load() {
        if (!caseReferencesFile.exists()) {
            return
        }

        val gson = Gson()

        caseReferencesFile.readLines().forEach { jsonStruct ->
            val testCase = try {
                gson.fromJson(jsonStruct, TestCase::class.java)
            } catch (e: JsonSyntaxException) {
                throw IOException(e)
            }

            testCase.screenshotPaths.forEach { path ->
                caseReferences[path] = testCase.testCasePath
            }
        }
    }
}

private class TestCase(
    @SerializedName("testCasePath")
    val testCasePath: String,
    @SerializedName("screenshotPaths")
    val screenshotPaths: List<String>
)
