package com.yandex.div.evaluable.multiplatform

import com.yandex.div.evaluable.Evaluable
import com.yandex.div.evaluable.EvaluableException
import com.yandex.div.evaluable.Evaluator
import com.yandex.div.evaluable.VariableProvider
import com.yandex.div.evaluable.function.BuiltinFunctionProvider
import com.yandex.div.evaluable.multiplatform.MultiplatformTestUtils.isForAndroidPlatform
import com.yandex.div.evaluable.multiplatform.MultiplatformTestUtils.parsePlatform
import com.yandex.div.evaluable.multiplatform.MultiplatformTestUtils.toListOfJSONObject
import com.yandex.div.evaluable.types.Color
import com.yandex.div.evaluable.types.DateTime
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
    private val evaluator = Evaluator(variableProvider, BuiltinFunctionProvider(variableProvider))
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
        val expectedValue = testCase.expectedValue
        if (expectedValue is Exception) {
            val actualValue = evalExpression()
            Assert.assertTrue(actualValue is Exception)
            val expectedMessage = expectedValue.message.takeIf { it?.isNotEmpty() == true } ?: return
            Assert.assertEquals(expectedMessage, (actualValue as Throwable).message)
        } else {
            val evalExpression = evalExpression()
            if (evalExpression is Throwable) {
                throw AssertionError(
                    "Expecting '${testCase.expectedValue}' at expression '${testCase.expression}' " +
                    "but got exception instead!", evalExpression)
            }
            Assert.assertEquals("expression: '${testCase.expression}'", testCase.expectedValue, evalExpression)
        }
    }

    private fun evalExpression(): Any {
        return try {
            evaluator.eval<Any>(Evaluable.prepare(testCase.expression))
        } catch (e: EvaluableException) {
            e
        }
    }

    data class ExpressionTestCase(
        val fileName: String,
        val name: String,
        val expression: String,
        val variables: List<TestVariable>,
        val platform: List<String>,
        val expectedValue: Any
    ) {
        override fun toString(): String {
            return name
        }
    }

    data class TestVariable(val name: String, val value: Any)

    data class TestUrl(val value: String) {
        override fun toString() = value
    }

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

            checkDuplicates(cases.asSequence() + errors.asSequence())

            return errors + cases.filter { it.error != null || isForAndroidPlatform(it.testCase?.platform) }
        }

        private fun parseTestCase(file: File, json: JSONObject): TestCaseOrError<ExpressionTestCase> {
            val name = try {
                json.getString(CASE_NAME_FIELD)
            } catch (e: JSONException) {
                return TestCaseOrError(TestCaseParsingError("???", file, json, e))
            }

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
                    json.getJSONObject(CASE_EXPECTED_VALUE_FIELD).let { expected ->
                        parseValue(expected)
                    }
                )

                return TestCaseOrError(testCase)
            } catch (e: JSONException) {
                return TestCaseOrError(TestCaseParsingError(name, file, json, e))
            }
        }

        private fun checkDuplicates(cases: Sequence<TestCaseOrError<ExpressionTestCase>>) {
            val duplicate = cases.mapNotNull { it.testCase }
                .groupingBy { it.fileName to it.name }
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
                VALUE_TYPE_URL -> TestUrl(json.getString(VALUE_FIELD))
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
                VALUE_TYPE_UNIT -> Unit
                VALUE_TYPE_ERROR -> EvaluableException(json.optString(VALUE_FIELD))
                else -> throw IllegalAccessException("Unknown variable type: $type")
            }
            return value
        }
    }
}

