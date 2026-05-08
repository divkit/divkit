package com.yandex.div.internal.parser

import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.internal.util.forEach
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.test.crossplatform.ParsingResult
import com.yandex.div.test.crossplatform.ParsingUtils.parseFiles
import com.yandex.div.test.crossplatform.isForAndroid
import com.yandex.div2.DivData
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner

@RunWith(ParameterizedRobolectricTestRunner::class)
class ParsingMultiplatformTest(testCaseParsingResult: ParsingResult<ParsingTestCase>) {

    private val testCase = testCaseParsingResult.getOrThrow()
    private val errors = mutableListOf<Throwable>()

    @Test
    fun run() {
        val logger = ParsingErrorLogger { errors += it }
        val env = DivParsingEnvironment(logger)

        val divData = runCatching {
            testCase.templates?.let { env.parseTemplates(it) }
            DivData(env, testCase.card)
        }.getOrNull()

        assertCardEquals(testCase.expectedResult.card, divData?.writeToJSON())
        Assert.assertEquals("Unexpected error count:", testCase.expectedResult.errorCount, errors.size)
    }

    private fun assertCardEquals(expected: JSONObject?, actual: JSONObject?) {
        expected ?: return Assert.assertNull(actual)
        actual ?: return Assert.fail(
            "Failed to parse DivData. Errors: ${errors.joinToString(", ") { it.message ?: "" }}"
        )

        runCatching {
            expected.compareValues("", actual)
        }.onFailure {
            Assert.fail(it.message)
        }
    }

    private fun JSONObject.compareValues(parentPath: String, actual: JSONObject): Boolean {
        forEach { key, expectedValue: Any? ->
            val actualValue = actual.opt(key)
            val path = parentPath.takeIf { it.isNotEmpty() }?.let { "$it/$key" } ?: key
            if (!expectedValue.compare(path, actualValue)) {
                throwError(path, expectedValue, actualValue)
            }
        }
        return true
    }

    private fun Any?.compare(path: String, actual: Any?): Boolean {
        return when (this) {
            JSONObject.NULL -> actual == null || actual == this
            is JSONObject -> (actual as? JSONObject)?.let { compareValues(path, it) } ?: false
            is JSONArray -> (actual as? JSONArray)?.let { compareValues(path, it)} ?: false
            is Int -> actual == this || (actual as? Long)?.toInt() == this
            else -> actual == this
        }
    }

    private fun JSONArray.compareValues(parentPath: String, actual: JSONArray): Boolean {
        forEach { i, expectedValue: Any? ->
            val actualValue = actual.opt(i)
            val path = parentPath.takeIf { it.isNotEmpty() }?.let { "$it/$i" } ?: "$i"
            if (!expectedValue.compare(path, actualValue)) {
                throwError(path, expectedValue, actualValue)
            }
        }
        return true
    }

    private fun throwError(path: String, expected: Any?, actual: Any?): Nothing =
        throw Exception("DivData comparison failed for path '$path', expected='$expected', actual='$actual'")

    companion object {
        // Store parsed test cases to prevent multiple parsing by
        // ParameterizedRobolectricTestRunner
        private val cases: List<ParsingResult<ParsingTestCase>> = run {
            parseFiles("parsing_test_data") { file, jsonString ->
                val json = JSONObject(jsonString)
                if (!json.isForAndroid) return@parseFiles emptyList()

                val result = try {
                    val testCase = ParsingTestCase.from(file.name, json)
                    ParsingResult.Success(testCase)
                } catch (e: Exception) {
                    ParsingResult.Error(fileName = file.name, error = e)
                }
                listOf(result)
            }
        }

        @JvmStatic
        @Suppress("unused")
        @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
        fun cases() = cases
    }
}
