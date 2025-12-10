package com.yandex.div.core.expression

import com.yandex.div.core.Div2Logger
import com.yandex.div.core.expression.ExpressionTestCaseUtils.VALUE_TYPE_UNORDERED_ARRAY
import com.yandex.div.core.expression.ExpressionTestCaseUtils.createDivDataFromTestVars
import com.yandex.div.core.expression.ExpressionTestCaseUtils.toEvaluableType
import com.yandex.div.core.expression.local.ExpressionsRuntimeProvider
import com.yandex.div.core.expression.local.RuntimeStore
import com.yandex.div.core.expression.storedvalues.StoredValuesController
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.core.view2.divs.DivActionBinder
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.rule.LocaleRule
import com.yandex.div.test.expression.MultiplatformTestUtils
import com.yandex.div.test.expression.MultiplatformTestUtils.toSortedList
import com.yandex.div.test.expression.TestCaseOrError
import org.json.JSONArray
import org.json.JSONObject
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.robolectric.ParameterizedRobolectricTestRunner
import com.yandex.div.internal.Assert as DivAssert

@RunWith(ParameterizedRobolectricTestRunner::class)
class EvaluableMultiplatformTest(private val caseOrError: TestCaseOrError<ExpressionTestCase>) {

    @Rule
    @JvmField
    val localeRule = LocaleRule()

    private lateinit var testCase: ExpressionTestCase

    private val mockDivVariableController = mock<DivVariableController> {
        on { variableSource } doReturn mock()
    }
    private val runtimeStore = mock<RuntimeStore> {
        on { viewProvider } doReturn mock()
    }

    private val warnings = mutableListOf<String>()
    private val errors = mutableListOf<String>()
    private val warningCaptor = argumentCaptor<Throwable>()
    private val errorCollector = mock<ErrorCollector> {
        on { logWarning(warningCaptor.capture()) } doAnswer {
            warningCaptor.lastValue.cause?.message?.let { warnings += it }
        }
    }
    private val testParsingLogger = ParsingErrorLogger { e ->
        var t: Throwable? = e
        while (t != null) {
            t.message?.let { errors += it }
            t = t.cause
        }
    }

    private lateinit var runtimeProvider: ExpressionsRuntimeProvider
    private lateinit var resolver: ExpressionResolverImpl

    @Before
    fun setUp() {
        warnings.clear()
        errors.clear()
        testCase = caseOrError.getCaseOrThrow()
        val testDivData = createDivDataFromTestVars(testCase.variables, testCase.functions, testParsingLogger)

        runtimeProvider = ExpressionsRuntimeProvider(
            mockDivVariableController,
            mock<DivActionBinder>(),
            mock<Div2Logger>(),
            mock<StoredValuesController>(),
        )

        val rootRuntime = runtimeProvider.createRootRuntime(
            data = testDivData,
            errorCollector = errorCollector,
            runtimeStore = runtimeStore,
        )
        resolver = rootRuntime.expressionResolver
    }

    @Test
    fun runExpressionTestCase() {
        when (val expectedValue = testCase.expectedValue) {
            is Exception -> {
                evalExpression()
                val expectedMessage =
                    expectedValue.message.takeIf { it?.isNotEmpty() == true } ?: return
                Assert.assertTrue(
                    "Expected error <$expectedMessage>, but got $errors",
                    errors.contains(expectedMessage)
                )
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
        val evalExpression = evalExpression()
        if (evalExpression is Throwable) {
            throw AssertionError(
                "Expecting '${testCase.expectedValue}' at expression '${testCase.expression}' " +
                    "but got exception instead!", evalExpression)
        }
        validate("expression: '${testCase.expression}'", testCase.expectedValue, evalExpression)
    }

    private fun evalExpression(): Any {
        val value = runCatching {
            DivExpressionParser.readTypedExpression(
                raw = testCase.expression,
                key = "value",
                expectedType = testCase.expectedType.toEvaluableType(),
                logger = testParsingLogger,
            ).evaluate(resolver)
        }.getOrElse { return it }

        val expectedSorted = testCase.expectedWarnings.sorted()
        val actualSorted = warnings.sorted()
        Assert.assertEquals(
            "Expected warnings: $expectedSorted, got: $actualSorted",
            expectedSorted,
            actualSorted
        )
        return value
    }

    companion object {

        private lateinit var assertStatic: MockedStatic<DivAssert>

        @JvmStatic
        @BeforeClass
        fun disableDivThreadAsserts() {
            assertStatic = Mockito.mockStatic(DivAssert::class.java)
        }

        @JvmStatic
        @AfterClass
        fun restoreDivThreadAsserts() {
            assertStatic.close()
        }

        private const val TEST_CASES_FILE_PATH = "expression_test_data"

        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
        fun cases(): List<TestCaseOrError<ExpressionTestCase>> {
            val cases = mutableListOf<TestCaseOrError<ExpressionTestCase>>()
            val errors = MultiplatformTestUtils.walkJSONs(TEST_CASES_FILE_PATH) { file, jsonString ->
                val newCases = ExpressionTestCaseUtils.parseTestCases(JSONObject(jsonString), file.name)
                cases.addAll(newCases)
            }.map { TestCaseOrError<ExpressionTestCase>(it) }

            val allCases = errors + cases

            ExpressionTestCaseUtils.checkDuplicates(allCases.asSequence())

            return allCases
        }
    }
}
