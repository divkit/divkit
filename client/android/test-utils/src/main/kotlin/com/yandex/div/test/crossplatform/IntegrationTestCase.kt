package com.yandex.div.test.crossplatform

import android.net.Uri
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.data.Variable
import com.yandex.div.internal.variables.name
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAction
import com.yandex.div2.DivData
import com.yandex.div2.DivVariable
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail

class IntegrationTestCase(
    private val name: String,
    private val divData: JSONObject,
    private val actions: List<JSONObject>,
    private val expectedResults: List<ExpectedResult>
) {

    sealed interface ExpectedResult {

        class Variable(
            val name: String,
            val type: String,
            private val value: Any
        ) : ExpectedResult {
            fun check(expressionResolver: ExpressionResolver) {
                val actualValue = expressionResolver.getVariable(name)?.getValue()
                if (type == "array" || type == "dict" || type == "url") {
                    assertEquals(value.toString(), actualValue.toString())
                } else {
                    assertEquals(value, actualValue)
                }
            }
        }

        class Error(private val message: String) : ExpectedResult {
            fun check(errors: List<String>) {
                assertTrue(
                    "Expected: <$message> but was: <${errors.toSet().joinToString(", ")}>",
                    errors.contains(message)
                )
            }
        }
    }

    val logger = IntegrationTestLogger()
    private val parsingEnvironment = DivParsingEnvironment(logger)

    override fun toString() = name

    fun parseDivData(): DivData? {
        divData.optJSONObject("templates")?.let {
            parsingEnvironment.parseTemplates(it)
        }

        try {
            return DivData(parsingEnvironment, divData.getJSONObject("card"))
        } catch (throwable: Throwable) {
            var isErrorExpected = false
            expectedResults
                .filterIsInstance<ExpectedResult.Error>()
                .forEach { result ->
                    result.check(logger.messages)
                    isErrorExpected = true
                }

            if (!isErrorExpected) {
                fail("Unexpected parsing error: ${throwable.message}")
            }
        }

        return null
    }

    fun parseActions(): List<DivAction> {
        return actions.map {
            DivAction(json = it, env = parsingEnvironment)
        }
    }

    /**
     * Declares variables that are used in expected results but no not declared in DivData.
     */
    fun declareResultVariables(
        variables: List<DivVariable>,
        variableController: DivVariableController
    ) {
        expectedResults
            .filterIsInstance<ExpectedResult.Variable>()
            .forEach { variable ->
                if (!variables.any { it.name == variable.name }) {
                    variableController.declare(createVariable(variable.type, variable.name))
                }
            }
    }

    fun checkResult(expressionResolver: ExpressionResolver) {
        expectedResults.forEach {
            when (it) {
                is ExpectedResult.Error ->
                    it.check(logger.messages)

                is ExpectedResult.Variable ->
                    it.check(expressionResolver = expressionResolver)
            }
        }
    }
}

private fun createVariable(type: String, name: String): Variable {
    return when (type) {
        "array" -> Variable.ArrayVariable(name, JSONArray())
        "boolean" -> Variable.BooleanVariable(name, false)
        "color" -> Variable.ColorVariable(name, 0)
        "dict" -> Variable.DictVariable(name, JSONObject())
        "integer" -> Variable.IntegerVariable(name, 0)
        "number" -> Variable.DoubleVariable(name, 0.0)
        "string" -> Variable.StringVariable(name, "")
        "url" -> Variable.UrlVariable(name, Uri.EMPTY)
        else -> throw IllegalAccessException("Unknown variable type: $type")
    }
}
