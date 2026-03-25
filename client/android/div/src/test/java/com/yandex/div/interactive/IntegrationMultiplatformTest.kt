package com.yandex.div.interactive

import android.app.Activity
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.actions.observeErrors
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.images.LoadReference
import com.yandex.div.core.view2.Div2View
import com.yandex.div.test.crossplatform.IntegrationTestCase
import com.yandex.div.test.crossplatform.IntegrationTestCaseParser
import com.yandex.div.test.crossplatform.ParsingResult
import com.yandex.div.test.crossplatform.ParsingUtils
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.Robolectric
import java.util.UUID

@RunWith(ParameterizedRobolectricTestRunner::class)
class IntegrationMultiplatformTest(testCaseParsingResult: ParsingResult<IntegrationTestCase>) {

    private val testCase = testCaseParsingResult.getOrThrow()
    private val activity = Robolectric.buildActivity(Activity::class.java).get()

    @Test
    fun run() {
        val divData = testCase.parseDivData() ?: return

        val context = Div2Context(activity, DivConfiguration.Builder(IMAGE_LOADER_STUB).build())

        testCase.declareResultVariables(
            variables = divData.variables ?: emptyList(),
            variableController = context.divVariableController
        )

        val divView = Div2View(context)
        divView.setData(divData, DivDataTag(UUID.randomUUID().toString()))
        divView.observeErrors { errors, _ ->
            errors.forEach { error ->
                testCase.logger.logErrorDirectly(error)
            }
        }

        testCase.parseActions().forEach {
            divView.handleAction(it)
        }

        testCase.checkResult(
            expressionResolver = divView.expressionResolver
        )
    }

    companion object {
        private val EMPTY_REF = LoadReference { }
        private val IMAGE_LOADER_STUB = DivImageLoader { _, _ -> EMPTY_REF }

        // Store parsed test cases to prevent multiple parsing by
        // ParameterizedRobolectricTestRunner
        private val cases: List<ParsingResult<IntegrationTestCase>> = run {
            ParsingUtils.parseFiles("integration_test_data") { file, json ->
                IntegrationTestCaseParser.parseCases(file.name, json)
            }
        }

        @JvmStatic
        @Suppress("unused")
        @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
        fun cases() = cases
    }
}
