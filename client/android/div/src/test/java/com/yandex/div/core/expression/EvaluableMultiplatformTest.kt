package com.yandex.div.core.expression

import com.yandex.div.core.expression.ExpressionTestCaseUtils.VALUE_TYPE_UNORDERED_ARRAY
import com.yandex.div.core.expression.ExpressionTestCaseUtils.toVariable
import com.yandex.div.core.expression.variables.wrapVariableValue
import com.yandex.div.evaluable.Evaluable
import com.yandex.div.evaluable.EvaluableException
import com.yandex.div.evaluable.EvaluationContext
import com.yandex.div.evaluable.VariableProvider
import com.yandex.div.evaluable.function.GeneratedBuiltinFunctionProvider
import com.yandex.div.test.expression.MultiplatformTestUtils
import com.yandex.div.test.expression.MultiplatformTestUtils.toSortedList
import com.yandex.div.test.expression.TestCaseOrError
import com.yandex.div.test.expression.withEvaluator
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

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
        val variables = testCase.variables.map { it.toVariable() }
        for (variable in variables) {
            whenever(variableProvider.get(variable.name)).thenReturn(variable.getValue().wrapVariableValue())
        }
    }

    @Test
    fun runExpressionTestCase() {
        if (testCase.isExpressionConstant) {
            Assert.assertEquals("expression: '${testCase.expression}'", testCase.expectedValue, testCase.expression)
            return
        }
        when (val expectedValue = testCase.expectedValue) {
            is Exception -> {
                val actualValue = evalExpression(testCase.expectedWarnings)
                Assert.assertTrue(actualValue is Exception)
                val expectedMessage =
                    expectedValue.message.takeIf { it?.isNotEmpty() == true } ?: return
                Assert.assertEquals(expectedMessage, (actualValue as Throwable).message)
            }

            is JSONArray, is JSONObject -> {
                if (testCase.expectedType == VALUE_TYPE_UNORDERED_ARRAY){
                    checkEquality(testCase) { message, expected, actual ->
                        val expectedList = (expected as JSONArray).toSortedList()
                        val actualList = (actual as JSONArray).toSortedList()
                        Assert.assertEquals(message, expectedList.toString(), actualList.toString())
                    }
                } else {
                    checkEquality(testCase) { message, expected, actual ->
                        Assert.assertEquals(message, expected.toString(), actual.toString())
                    }
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

    companion object {
        private const val TEST_CASES_FILE_PATH = "expression_test_data"

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun cases(): List<TestCaseOrError<ExpressionTestCase>> {
            val cases = mutableListOf<TestCaseOrError<ExpressionTestCase>>()
            val errors = MultiplatformTestUtils.walkJSONs(TEST_CASES_FILE_PATH) { file, json ->
                val newCases = ExpressionTestCaseUtils.parseTestCases(json, file.name)
                cases.addAll(newCases)
            }.map { TestCaseOrError<ExpressionTestCase>(it) }

            val allCases = errors + cases

            ExpressionTestCaseUtils.checkDuplicates(allCases.asSequence())

            return allCases
        }
    }
}
