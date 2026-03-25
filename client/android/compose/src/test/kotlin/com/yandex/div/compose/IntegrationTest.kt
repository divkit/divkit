package com.yandex.div.compose

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.test.crossplatform.IntegrationTestCase
import com.yandex.div.test.crossplatform.IntegrationTestCaseParser
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
        val divContext = DivComposeConfiguration(
            reporter = TestReporter(),
            variableController = variableController
        ).createContext(baseContext = getApplicationContext())

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
            divContext.debugFeatures.performAction(it)
        }

        testCase.checkResult(
            expressionResolver = divContext.debugFeatures.expressionResolver!!
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

private val ignoredFiles = listOf(
    "array_variable_mutation.json",
    "decl_expressions_item_builder.json",
    "decl_expressions_item_builder_override.json",
    "dict_set_value.json",
    "expression_with_several_local_functions.json",
    "item_builder_variable_triggers.json",
    "local_functions_array.json",
    "local_functions_datetime.json",
    "local_functions_color.json",
    "local_functions_dict.json",
    "local_functions_div_data.json",
    "local_functions_int.json",
    "local_functions_number.json",
    "local_functions_string.json",
    "local_functions_url.json",
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
    "update_structure.json",
    "wrap_content_constraints_warning.json",
)
