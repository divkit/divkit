package com.yandex.div.core

import com.yandex.div.BuildConfig.DIV2_JSON_PATH
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
        val underTest = testCase.json.opt(UNSUPPORTED_PLATFORMS) ?: return
        val platformsDict = underTest as? JSONObject
                ?: throw AssertionError("Expecting a dict at '$UNSUPPORTED_PLATFORMS' field!")

        platformsDict.keys().forEach { key ->
            Assert.assertTrue("Expecting a dict with strings at '$UNSUPPORTED_PLATFORMS'!",
                    platformsDict.get(key) is String)
        }
    }

    companion object {
        private const val UNSUPPORTED_PLATFORMS = "unsupported_platforms"
        private const val CASES_FIELD = "cases"
        private const val SIGNATURES_FIELD = "signatures"
        private const val CASE_NAME_FIELD = "name"
        private const val TEST_CASES_FILE_PATH = DIV2_JSON_PATH

        private val TEST_CASES_ROOT = File(TEST_CASES_FILE_PATH)

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun cases(): List<TestCaseOrError> {
            val cases = mutableListOf<TestCaseOrError>()
            cases.addAll(walkIn("expression_test_data").flatMap { f -> extractExpressionCases(f) })
            cases.addAll(walkIn("interactive_snapshot_test_data").map { f -> toCommonUiTestCase(f) })
            cases.addAll(walkIn("regression_test_data").map { f -> toCommonUiTestCase(f) })
            cases.addAll(walkIn("snapshot_test_data").map { f -> toCommonUiTestCase(f) })
            cases.addAll(walkIn("ui_test_data").map { f -> toCommonUiTestCase(f) })
            return cases
        }

        private fun walkIn(dir: String) = File(TEST_CASES_FILE_PATH, dir)
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
            val cases = root.optJSONArray(CASES_FIELD)
                    .toListOfJSONObject()
                    .map {
                        TestCaseOrError.TestCase(
                            path = f.toTestPath() + ":" + it.optString(CASE_NAME_FIELD),
                            json = it)
                    }
            val signatures = root.optJSONArray(SIGNATURES_FIELD)
                    .toListOfJSONObject()
                    .map {
                        TestCaseOrError.TestCase(
                            path = f.toTestPath() + ":" + it.optString(CASE_NAME_FIELD),
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
