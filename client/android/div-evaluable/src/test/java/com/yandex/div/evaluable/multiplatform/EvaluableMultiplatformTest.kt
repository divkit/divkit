package com.yandex.div.evaluable.multiplatform

import com.yandex.div.evaluable.Evaluable
import com.yandex.div.evaluable.EvaluableException
import com.yandex.div.evaluable.EvaluationContext
import com.yandex.div.evaluable.VariableProvider
import com.yandex.div.evaluable.function.GeneratedBuiltinFunctionProvider
import com.yandex.div.evaluable.multiplatform.MultiplatformTestUtils.isForAndroidPlatform
import com.yandex.div.evaluable.multiplatform.MultiplatformTestUtils.parsePlatform
import com.yandex.div.evaluable.multiplatform.MultiplatformTestUtils.toListOfJSONObject
import com.yandex.div.evaluable.types.Color
import com.yandex.div.evaluable.types.DateTime
import com.yandex.div.evaluable.types.Url
import com.yandex.div.evaluable.withEvaluator
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.io.File

@RunWith(Parameterized::class)
class EvaluableMultiplatformTest(private val caseOrError: TestCaseOrError<ExpressionTestCase>) {

    private val variableProvider = mock<VariableProvider>()
    private val evaluationContext = EvaluationContext(
        variableProvider = variableProvider,
        storedValueProvider = mock(),
        functionProvider = GeneratedBuiltinFunctionProvider,
        warningSender = { _, _ ->  }
    )
    private lateinit var testCase: ExpressionTestCase

    @Before
    fun setUp() {
        testCase = caseOrError.getCaseOrThrow()
        for (variable in testCase.variables) {
            whenever(variableProvider.get(variable.name)).thenReturn(variable.value)
        }
    }

    @Test
    fun runExpressionTestCase() {
        when (val expectedValue = testCase.expectedValue) {
            is Exception -> {
                val actualValue = evalExpression(testCase.expectedWarnings)
                Assert.assertTrue(actualValue is Exception)
                val expectedMessage =
                    expectedValue.message.takeIf { it?.isNotEmpty() == true } ?: return
                Assert.assertEquals(expectedMessage, (actualValue as Throwable).message)
            }

            is JSONArray, is JSONObject -> {
                checkEquality(testCase) { message, expected, actual ->
                    Assert.assertEquals(message, expected.toString(), actual.toString())
                }
            }

            is Double -> {
                checkEquality(testCase) { message, expected, actual ->
                    Assert.assertEquals(message, expected as Double, actual as Double, 1.0e-10)
                }
            }

            else -> {
                checkEquality(testCase) { message, expected, actual ->
                    Assert.assertEquals(message, expected, actual)
                }
            }
        }
    }

    private fun checkEquality(testCase: ExpressionTestCase, validate: (String, Any, Any) -> Unit) {
        val evalExpression = evalExpression(testCase.expectedWarnings)
        if (evalExpression is Throwable) {
            throw AssertionError(
                "Expecting '${testCase.expectedValue}' at expression '${testCase.expression}' " +
                    "but got exception instead!", evalExpression)
        }
        validate("expression: '${testCase.expression}'", testCase.expectedValue, evalExpression)
    }

    private fun evalExpression(expectedWarnings: List<String>): Any {
        return try {
            withEvaluator(
                evaluationContext,
                warningsValidator = { actualWarnings ->
                    val expectedSorted = expectedWarnings.sorted()
                    val actualSorted = actualWarnings.sorted()

                    Assert.assertEquals(
                        "Expected warnings: $expectedWarnings, got: $actualWarnings",
                        expectedSorted,
                        actualSorted
                    )
                }
            ) {
                eval<Any>(Evaluable.prepare(testCase.expression))
            }
        } catch (e: EvaluableException) {
            e
        }
    }

    data class ExpressionTestCase(
        val fileName: String,
        val name: String?,
        val expression: String,
        val variables: List<TestVariable>,
        val platform: List<String>,
        val expectedValue: Any,
        val expectedWarnings: List<String>
    ) {
        val description: String
            get() {
                val formattedExpression = if (expression.startsWith("@{")
                    && expression.endsWith("}")
                    && !expression.drop(2).contains("@{")) {
                    expression.drop(2).dropLast(1)
                } else {
                    expression
                }
                val result: String = when (expectedValue) {
                    is String -> "'$expectedValue'"
                    is JSONArray -> "<array>"
                    is JSONObject -> "<dict>"
                    is Exception -> "error"
                    else -> expectedValue.toString()
                }
                return name ?: "$formattedExpression -> $result"
            }

        override fun toString(): String {
            return description
        }
    }

    data class TestVariable(val name: String, val value: Any)

    data class TestDate(val value: String) {
        private val dateTime = DateTime.parseAsUTC(value)

        override fun equals(other: Any?): Boolean {
            return dateTime == other
        }

        override fun hashCode(): Int {
            return dateTime.hashCode()
        }

        override fun toString() = value
    }

    companion object {
        private const val TEST_CASES_FILE_PATH = "expression_test_data"

        private const val CASES_FIELD = "cases"
        private const val CASE_NAME_FIELD = "name"
        private const val CASE_VARIABLES_FIELD = "variables"
        private const val CASE_VARIABLE_NAME_FIELD = "name"
        private const val CASE_EXPECTED_VALUE_FIELD = "expected"
        private const val CASE_EXPECTED_WARNINGS_FIELD = "expected_warnings"
        private const val CASE_EXPRESSION_VALUE_FIELD = "expression"
        private const val TYPE_FIELD = "type"
        private const val VALUE_FIELD = "value"

        private const val VALUE_TYPE_STRING = "string"
        private const val VALUE_TYPE_INTEGER = "integer"
        private const val VALUE_TYPE_DECIMAL = "number"
        private const val VALUE_TYPE_BOOLEAN = "boolean"
        private const val VALUE_TYPE_BOOL_INT = "bool_int"
        private const val VALUE_TYPE_DATE_TIME = "datetime"
        private const val VALUE_TYPE_URL = "url"
        private const val VALUE_TYPE_COLOR = "color"
        private const val VALUE_TYPE_DICT = "dict"
        private const val VALUE_TYPE_ARRAY = "array"
        private const val VALUE_TYPE_UNIT = "unit"
        private const val VALUE_TYPE_ERROR = "error"

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun cases(): List<TestCaseOrError<ExpressionTestCase>> {
            val cases = mutableListOf<TestCaseOrError<ExpressionTestCase>>()
            val errors = MultiplatformTestUtils.walkJSONs(TEST_CASES_FILE_PATH) { file, json ->
                val newCases = json.optJSONArray(CASES_FIELD)
                    .toListOfJSONObject()
                    .map { parseTestCase(file, it) }
                cases.addAll(newCases)
            }.map { TestCaseOrError<ExpressionTestCase>(it) }

            val allCases = errors + cases.filter {
                it.error != null || isForAndroidPlatform(it.testCase?.platform)
            }

            checkDuplicates(allCases.asSequence())

            return allCases
        }

        private fun parseTestCase(file: File, json: JSONObject): TestCaseOrError<ExpressionTestCase> {
            val name = json.optString(CASE_NAME_FIELD)
            try {
                val testCase = ExpressionTestCase(
                    file.name,
                    name,
                    json.getString(CASE_EXPRESSION_VALUE_FIELD),
                    json.optJSONArray(CASE_VARIABLES_FIELD)?.let { array ->
                        val result = mutableListOf<TestVariable>()
                        for (i in 0 until array.length()) {
                            val variable = array.getJSONObject(i)
                            val value: Any = parseValue(variable)
                            result.add(
                                TestVariable(
                                    variable.getString(CASE_VARIABLE_NAME_FIELD),
                                    value
                                )
                            )
                        }
                        result
                    } ?: emptyList(),
                    parsePlatform(json),
                    parseValue(json.getJSONObject(CASE_EXPECTED_VALUE_FIELD)),
                    json.optJSONArray(CASE_EXPECTED_WARNINGS_FIELD)?.map { it as String } ?: emptyList()
                )

                return TestCaseOrError(testCase)
            } catch (e: JSONException) {
                return TestCaseOrError(TestCaseParsingError(name, file, json, e))
            }
        }

        private fun checkDuplicates(cases: Sequence<TestCaseOrError<ExpressionTestCase>>) {
            val duplicate = cases.mapNotNull { it.testCase }
                .groupingBy { it.fileName to it.description }
                .eachCount()
                .filterValues { it > 1 }
                .keys

            assert(duplicate.isEmpty()) {
                duplicate.joinToString("\n", prefix = "Duplicate test case names:\n") {
                    it.toList().joinToString("::")
                }
            }
        }

        private fun parseValue(json: JSONObject): Any {
            val value: Any = when (val type = json.getString(TYPE_FIELD)) {
                VALUE_TYPE_STRING -> json.getString(VALUE_FIELD)
                VALUE_TYPE_URL -> Url.from(json.getString(VALUE_FIELD))
                VALUE_TYPE_COLOR -> Color.parse(json.getString(VALUE_FIELD))
                VALUE_TYPE_INTEGER -> json.getLong(VALUE_FIELD)
                VALUE_TYPE_DECIMAL -> json.getDouble(VALUE_FIELD)
                VALUE_TYPE_BOOLEAN,
                VALUE_TYPE_BOOL_INT -> {
                    val value = json.get(VALUE_FIELD)
                    return when {
                        value is Number -> value.toInt() == 1
                        value == java.lang.Boolean.FALSE -> false
                        value == java.lang.Boolean.TRUE -> true
                        value is String && "true".equals(value, true) -> true
                        value is String && "false".equals(value, true) -> false
                        else -> throw IllegalAccessException("Unknown variable value: $value")
                    }
                }
                VALUE_TYPE_DATE_TIME -> TestDate(json.getString(VALUE_FIELD))
                VALUE_TYPE_DICT -> json.getJSONObject(VALUE_FIELD)
                VALUE_TYPE_ARRAY -> json.getJSONArray(VALUE_FIELD)
                VALUE_TYPE_UNIT -> Unit
                VALUE_TYPE_ERROR -> EvaluableException(json.optString(VALUE_FIELD))
                else -> throw IllegalAccessException("Unknown variable type: $type")
            }
            return value
        }
    }
}
