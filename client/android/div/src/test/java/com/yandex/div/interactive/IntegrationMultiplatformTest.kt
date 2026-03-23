package com.yandex.div.interactive

import android.app.Activity
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.actions.observeErrors
import com.yandex.div.core.expression.ExpressionTestCaseUtils.VALUE_TYPE_ARRAY
import com.yandex.div.core.expression.ExpressionTestCaseUtils.VALUE_TYPE_DICT
import com.yandex.div.core.expression.ExpressionTestCaseUtils.createVariable
import com.yandex.div.core.expression.getWrappedValue
import com.yandex.div.core.expression.name
import com.yandex.div.core.expression.variables.wrapVariableValue
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.images.LoadReference
import com.yandex.div.core.view2.Div2View
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.test.crossplatform.IntegrationTestCase
import com.yandex.div.test.crossplatform.MultiplatformTestUtils
import com.yandex.div.test.crossplatform.ParsingResult
import com.yandex.div2.DivAction
import com.yandex.div2.DivData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.Robolectric
import java.util.UUID

@RunWith(ParameterizedRobolectricTestRunner::class)
class IntegrationMultiplatformTest(testCaseParsingResult: ParsingResult<IntegrationTestCase>) {

    private val testCase = testCaseParsingResult.getOrThrow()
    private val expectedResults = testCase.expectedResults
    private val activity = Robolectric.buildActivity(Activity::class.java).get()
    private val logger = IntegrationTestLogger()

    @Test
    fun run() {
        val env = DivParsingEnvironment(logger)
        testCase.divData.optJSONObject("templates")?.let {
            env.parseTemplates(it)
        }
        val divData = runCatching {
            DivData(env, testCase.divData.getJSONObject("card"))
        }.getOrElse {
            var errorIsExpected = false
            expectedResults.forEach { e ->
                if (e !is IntegrationTestCase.ExpectedResult.Error) return@forEach
                checkError(e)
                errorIsExpected = true
            }

            if (!errorIsExpected) {
                throw AssertionError("Got unexpected error at data parsing!", it)
            }
            return
        }

        val context = Div2Context(activity, DivConfiguration.Builder(IMAGE_LOADER_STUB).build())

        expectedResults.forEach { result ->
            when (result) {
                is IntegrationTestCase.ExpectedResult.Variable -> {
                    if (divData.variables?.any { it.name == result.name } != true) {
                        val variable = createVariable(result.type, result.name, null)
                        context.divVariableController.declare(variable)
                    }
                }

                is IntegrationTestCase.ExpectedResult.Error -> return@forEach
            }
        }

        val divView = Div2View(context)
        divView.setData(divData, DivDataTag(UUID.randomUUID().toString()))
        divView.observeErrors { errors, _ ->
            errors.forEach { error ->
                logger.logErrorDirectly(error)
            }
        }

        testCase.actions.forEach {
            runCatching { divView.handleAction(DivAction(env, it)) }
        }

        expectedResults.forEach {
            when (it) {
                is IntegrationTestCase.ExpectedResult.Error -> checkError(it)
                is IntegrationTestCase.ExpectedResult.Variable -> {
                    val expectedValue = it.value.wrapVariableValue()
                    val actualValue = divView.expressionResolver
                        .getVariable(it.name)
                        ?.getWrappedValue()
                    if (it.type == VALUE_TYPE_DICT || it.type == VALUE_TYPE_ARRAY) {
                        assertEquals(expectedValue.toString(), actualValue.toString())
                    } else {
                        assertEquals(expectedValue, actualValue)
                    }
                }
            }
        }
    }

    private fun checkError(expected: IntegrationTestCase.ExpectedResult.Error) {
        assertTrue(
            "Expected: <${expected.message}> but was: <${
                logger.messages.toSet().joinToString(", ")
            }>",
            logger.messages.contains(expected.message)
        )
    }

    companion object {
        private const val TEST_CASES_FILE_PATH = "integration_test_data"
        private val EMPTY_REF = LoadReference { }
        private val IMAGE_LOADER_STUB = DivImageLoader { _, _ -> EMPTY_REF }

        // Store parsed test cases to prevent multiple parsing by
        // ParameterizedRobolectricTestRunner
        private val cases: List<ParsingResult<IntegrationTestCase>> = run {
            val cases = mutableListOf<ParsingResult<IntegrationTestCase>>()
            val errors = MultiplatformTestUtils
                .walkJSONs(TEST_CASES_FILE_PATH) { file, json ->
                    cases.addAll(IntegrationTestCase.parse(file.name, json))
                }
            errors + cases
        }

        @JvmStatic
        @Suppress("unused")
        @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
        fun cases() = cases
    }
}
