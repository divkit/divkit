package com.yandex.div.compose

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.test.crossplatform.IntegrationTestCase
import com.yandex.div.test.crossplatform.IntegrationTestCaseParser
import com.yandex.div.test.crossplatform.IntegrationTestLogger
import com.yandex.div.test.crossplatform.ParsingResult
import com.yandex.div.test.crossplatform.ParsingUtils
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner

@RunWith(ParameterizedRobolectricTestRunner::class)
class IntegrationTest(testCaseParsingResult: ParsingResult<IntegrationTestCase>) {

    private val testCase = testCaseParsingResult.getOrThrow()

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun run() {
        val divData = testCase.parseDivData() ?: return

        val variableController = DivVariableController()
        val divContext = DivContext(
            baseContext = getApplicationContext(),
            configuration = DivComposeConfiguration(
                reporter = Reporter(testCase.logger),
                variableController = variableController
            )
        )

        testCase.declareResultVariables(
            variables = divData.variables ?: emptyList(),
            variableController = variableController
        )

        rule.setContent {
            CompositionLocalProvider(LocalContext provides divContext) {
                DivView(data = divData)
            }
        }

        testCase.parseActions().forEach {
            divContext.debugFeatures.performAction(data = divData, action = it)
        }

        testCase.checkResult(
            expressionResolver = divContext.debugFeatures.getExpressionResolver(divData)!!
        )
    }

    companion object {
        // Store parsed test cases to prevent multiple parsing by
        // ParameterizedRobolectricTestRunner
        private val cases: List<ParsingResult<IntegrationTestCase>> = run {
            ParsingUtils.parseFiles("integration_test_data") { file, json ->
                if (ignoredFiles.contains(file.name)) {
                    emptyList()
                } else {
                    IntegrationTestCaseParser.parseCases(file.name, json)
                }
            }
        }

        @JvmStatic
        @Suppress("unused")
        @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
        fun cases() = cases
    }
}

private class Reporter(private val logger: IntegrationTestLogger) : DivReporter() {
    override fun reportError(message: String) {
        logger.logError(Exception(message))
    }

    override fun reportError(e: Exception) {
        logger.logError(e)
    }
}

private val ignoredFiles = listOf(
    "decl_expressions_item_builder.json",
    "decl_expressions_item_builder_override.json",
    "item_builder_variable_triggers.json",
    "local-triggers-gallery.json",
    "local-triggers-gallery-with-item-builder.json",
    "local-triggers-states.json",
    "local-triggers-tabs.json",
    "properties_cycled.json",
    "property_boolean_value_from_dict.json",
    "property_color_value_from_array.json",
    "property_color_value_from_dict.json",
    "property_cycle.json",
    "property_integer_value_from_dict.json",
    "property_new_value_variable_name.json",
    "property_number_value_from_dict.json",
    "property_string_value_and_variable_from_dict.json",
    "property_string_value_from_dict.json",
    "property_string_value_from_variable.json",
    "property_url_value_from_dict.json",
    "property_without_setter.json",
    "wrap_content_constraints_warning.json",
)
