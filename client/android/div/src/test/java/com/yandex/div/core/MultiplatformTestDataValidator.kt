package com.yandex.div.core

import com.yandex.div.BuildConfig.DIV2_JSON_PATH
import com.yandex.div.BuildConfig.EXPRESSION_API_PATH
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File


@RunWith(Parameterized::class)
class MultiplatformTestDataValidator(caseOrError: TestCaseOrError) {
    private val testCase: TestCaseOrError.TestCase = caseOrError.getCaseOrThrow()

    @Test
    fun `validate unsupported_platforms scheme`() {
        val underTest = testCase.json.opt(FIELD_UNSUPPORTED_PLATFORMS) ?: return
        val platformsDict = underTest as? JSONObject
                ?: throw AssertionError("Expecting a dict at '$FIELD_UNSUPPORTED_PLATFORMS' field!")

        platformsDict.keys().forEach { key ->
            Assert.assertTrue("Expecting a dict with strings at '$FIELD_UNSUPPORTED_PLATFORMS'!",
                    platformsDict.get(key) is String)
        }
    }

    companion object {
        private const val FIELD_UNSUPPORTED_PLATFORMS = "unsupported_platforms"
        private const val FIELD_CASES = "cases"
        private const val FIELD_SIGNATURES = "signatures"
        private const val FIELD_CASE_NAME = "name"
        private const val FIELD_ARGUMENTS = "arguments"
        private const val FIELD_TYPE = "type"

        private val TEST_CASES_ROOT = File(DIV2_JSON_PATH)

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun cases(): List<TestCaseOrError> {
            val cases = mutableListOf<TestCaseOrError>()
            cases.addAll(walkIn(File(EXPRESSION_API_PATH)).flatMap { f -> extractExpressionCases(f) })
            cases.addAll(walkIn("expression_test_data").flatMap { f -> extractExpressionCases(f) })
            cases.addAll(walkIn("interactive_snapshot_test_data").map { f -> toCommonUiTestCase(f) })
            cases.addAll(walkIn("regression_test_data").map { f -> toCommonUiTestCase(f) })
            cases.addAll(walkIn("snapshot_test_data").map { f -> toCommonUiTestCase(f) })
            cases.addAll(walkIn("ui_test_data").map { f -> toCommonUiTestCase(f) })
            return cases
        }

        private fun walkIn(dir: String): Sequence<File> = walkIn(File(DIV2_JSON_PATH, dir))

        private fun walkIn(directory: File): Sequence<File> = directory
            .walkTopDown()
            .filter { it.extension == "json" }

        private fun extractExpressionCases(f: File): List<TestCaseOrError> {
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

            val results = mutableListOf<TestCaseOrError>()
            val root = try {
                JSONObject(f.readText())
            } catch (e: JSONException) {
                return listOf(TestCaseOrError.Error(f.toTestPath(), e))
            }
            val cases = root.optJSONArray(FIELD_CASES)
                    .toListOfJSONObject()
                    .map {
                        TestCaseOrError.TestCase(
                            path = f.toTestPath() + ":" + it.optString(FIELD_CASE_NAME),
                            json = it)
                    }
            val signatures = root.optJSONArray(FIELD_SIGNATURES)
                    .toListOfJSONObject()
                    .map {
                        val args = it.optJSONArray(FIELD_ARGUMENTS)
                            .toListOfJSONObject()
                            .mapNotNull { arg -> arg.optString(FIELD_TYPE) }
                        TestCaseOrError.TestCase(
                            path = "${f.toTestPath()}:${it.optString(FIELD_CASE_NAME)}(${args.joinToString()})",
                            json = it)
                    }
            results.addAll(cases)
            results.addAll(signatures)
            return results
        }

        private fun toCommonUiTestCase(f: File): TestCaseOrError {
            val root = try {
                JSONObject(f.readText())
            } catch (e: JSONException) {
                return TestCaseOrError.Error(f.toTestPath(), e)
            }

            return TestCaseOrError.TestCase(f.toTestPath(), root)
        }

        private fun File.toTestPath(): String {
            return this.relativeTo(TEST_CASES_ROOT).toString()
        }
    }
}

sealed interface TestCaseOrError {
    class Error(val path: String, val e: Throwable): TestCaseOrError {
        override fun toString(): String {
            return path
        }
    }

    class TestCase(val path: String, val json: JSONObject): TestCaseOrError {
        override fun toString(): String {
            return path
        }
    }

    fun getCaseOrThrow(): TestCase {
        return when (this) {
            is Error -> throw e
            is TestCase -> this
        }
    }
}
