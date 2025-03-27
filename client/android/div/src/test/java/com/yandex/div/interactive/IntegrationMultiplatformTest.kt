package com.yandex.div.interactive

import android.app.Activity
import android.widget.ImageView
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.expression.ExpressionTestCaseUtils
import com.yandex.div.core.expression.ExpressionTestCaseUtils.createVariable
import com.yandex.div.core.images.DivImageDownloadCallback
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.images.LoadReference
import com.yandex.div.core.view2.Div2View
import com.yandex.div.test.expression.MultiplatformTestUtils
import com.yandex.div.test.expression.TestCaseOrError
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
    private val logger = case.logger
    private val expected = case.expected
    private val activity = Robolectric.buildActivity(Activity::class.java).get()

    @Test
    fun runTestCase() {
        val context = Div2Context(activity, DivConfiguration.Builder(IMAGE_LOADER_STUB).build())
        val variableController = context.divVariableController
        expected.forEach {
            val variable = it as? IntegrationTestCase.ExpectedResult.Variable ?: return@forEach
            variableController.declare(createVariable(variable.type, variable.name, null))
        }

        val divView = Div2View(context)
        divView.setData(case.divData, DivDataTag(UUID.randomUUID().toString()))

        case.actions?.forEach {
            divView.handleAction(it)
        }

        expected.forEach {
            when (it) {
                is IntegrationTestCase.ExpectedResult.Error -> {
                    Assert.assertTrue(
                        "Expected: <${it.message}> but was: <${logger.messages.toSet().joinToString(", ")}>",
                        logger.messages.contains(it.message)
                    )
                }
                is IntegrationTestCase.ExpectedResult.Variable ->
                    Assert.assertEquals(it.value, variableController.get(it.name)?.getValue())
            }
        }
    }

    @After
    fun clear() = logger.clear()

    companion object {
        private const val TEST_CASES_FILE_PATH = "integration_test_data"
        private val EMPTY_REF = LoadReference { }
        private val IMAGE_LOADER_STUB = object : DivImageLoader {
            override fun loadImage(imageUrl: String, callback: DivImageDownloadCallback) = EMPTY_REF
            override fun loadImage(imageUrl: String, imageView: ImageView) = EMPTY_REF
            override fun loadImageBytes(imageUrl: String, callback: DivImageDownloadCallback) = EMPTY_REF
        }

        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
        fun cases(): List<TestCaseOrError<IntegrationTestCase>> {
            val logger = IntegrationTestLogger()
            val cases = mutableListOf<TestCaseOrError<IntegrationTestCase>>()
            val errors = MultiplatformTestUtils.walkJSONs(TEST_CASES_FILE_PATH) { file, json ->
                val steps = ExpressionTestCaseUtils.parseIntegrationTestCase(file.name, json, logger)
                cases.addAll(steps)
            }.map { TestCaseOrError<IntegrationTestCase>(it) }
            return errors + cases
        }
    }
}
