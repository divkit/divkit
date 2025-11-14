package com.yandex.div.interactive

import android.app.Activity
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.actions.observeErrors
import com.yandex.div.core.expression.ExpressionTestCaseUtils
import com.yandex.div.core.expression.ExpressionTestCaseUtils.VALUE_TYPE_ARRAY
import com.yandex.div.core.expression.ExpressionTestCaseUtils.VALUE_TYPE_DICT
import com.yandex.div.core.expression.ExpressionTestCaseUtils.createVariable
import com.yandex.div.core.expression.local.variableController
import com.yandex.div.core.expression.name
import com.yandex.div.core.expression.variables.wrapVariableValue
import com.yandex.div.core.images.DivImageDownloadCallback
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.images.LoadReference
import com.yandex.div.core.view2.Div2View
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.test.expression.MultiplatformTestUtils
import com.yandex.div.test.expression.TestCaseOrError
import com.yandex.div2.DivAction
import com.yandex.div2.DivData
import org.junit.After
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.Robolectric
import java.util.UUID

@RunWith(ParameterizedRobolectricTestRunner::class)
class IntegrationMultiplatformTest(testCase: TestCaseOrError<IntegrationTestCase>) {

    private val case = testCase.getCaseOrThrow()
    private val expected = case.expected
    private val activity = Robolectric.buildActivity(Activity::class.java).get()

    @Test
    fun runTestCase() {
        val env = DivParsingEnvironment(LOGGER)
        case.divData.optJSONObject("templates")?.let {
            env.parseTemplates(it)
        }
        val divData = runCatching {
            DivData(env, case.divData.getJSONObject("card"))
        }.getOrElse {
            expected.forEach {
                if (it !is IntegrationTestCase.ExpectedResult.Error) return@forEach
                checkError(it)
            }
            return
        }

        val context = Div2Context(activity, DivConfiguration.Builder(IMAGE_LOADER_STUB).build())
        expected.forEach { result ->
            val variable = result as? IntegrationTestCase.ExpectedResult.Variable ?: return@forEach
            if (divData.variables?.any { it.name == variable.name } == true) return@forEach
            context.divVariableController.declare(createVariable(variable.type, variable.name, null))
        }

        val divView = Div2View(context)
        divView.setData(divData, DivDataTag(UUID.randomUUID().toString()))
        divView.observeErrors { errors, _ ->
            errors.forEach { error ->
                LOGGER.logErrorDirectly(error)
            }
        }

        case.actions?.forEach {
            runCatching { divView.handleAction(DivAction(env, it)) }
        }

        expected.forEach {
            when (it) {
                is IntegrationTestCase.ExpectedResult.Error -> checkError(it)
                is IntegrationTestCase.ExpectedResult.Variable -> {
                    val expectedValue = it.value.wrapVariableValue()
                    val actualValue = divView.expressionResolver.variableController?.get(it.name)
                    if (it.type == VALUE_TYPE_DICT || it.type == VALUE_TYPE_ARRAY) {
                        Assert.assertEquals(expectedValue.toString(), actualValue.toString())
                    } else {
                        Assert.assertEquals(expectedValue, actualValue)
                    }
                }
            }
        }
    }

    private fun checkError(expected: IntegrationTestCase.ExpectedResult.Error) {
        Assert.assertTrue(
            "Expected: <${expected.message}> but was: <${LOGGER.messages.toSet().joinToString(", ")}>",
            LOGGER.messages.contains(expected.message)
        )
    }

    @After
    fun clear() = LOGGER.clear()

    companion object {
        private const val TEST_CASES_FILE_PATH = "integration_test_data"
        private val EMPTY_REF = LoadReference { }
        private val IMAGE_LOADER_STUB = object : DivImageLoader {
            override fun loadImage(imageUrl: String, callback: DivImageDownloadCallback) = EMPTY_REF
            override fun loadImageBytes(imageUrl: String, callback: DivImageDownloadCallback) = EMPTY_REF
        }

        private val LOGGER = IntegrationTestLogger()
        private val CASES = getCases()

        @JvmStatic
        @Suppress("unused")
        @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
        fun cases() = CASES

        private fun getCases(): List<TestCaseOrError<IntegrationTestCase>> {
            val cases = mutableListOf<TestCaseOrError<IntegrationTestCase>>()
            val errors = MultiplatformTestUtils.walkJSONs(TEST_CASES_FILE_PATH) { file, json ->
                val steps = ExpressionTestCaseUtils.parseIntegrationTestCase(file.name, json)
                cases.addAll(steps)
            }.map { TestCaseOrError<IntegrationTestCase>(it) }
            return errors + cases
        }
    }
}
