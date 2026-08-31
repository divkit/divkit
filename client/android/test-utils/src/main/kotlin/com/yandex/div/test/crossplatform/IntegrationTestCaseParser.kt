package com.yandex.div.test.crossplatform

import com.yandex.div.evaluable.types.Color
import org.json.JSONException
import org.json.JSONObject

object IntegrationTestCaseParser {

    fun parseCases(
        fileName: String,
        json: JSONObject
    ): List<ParsingResult<IntegrationTestCase>> {
        val jsonString = json.toString()
        val cases = json.optJSONArray("cases")?.toObjectList() ?: listOf(json)
        return cases
            .filter { it.isForAndroid }
            .mapIndexed { index, jsonObject ->
                try {
                    val testCase = jsonObject.parseTestCase(
                        fileName = fileName,
                        index = index,
                        // Fresh instance is required for every test case since JSONObject
                        // may contain mutable elements (array and dict variables).
                        divData = JSONObject(jsonString).getJSONObject("div_data")
                    )
                    ParsingResult.Success(testCase)
                } catch (e: Exception) {
                    ParsingResult.Error(fileName = fileName, error = e)
                }
            }
    }
}

private fun JSONObject.parseTestCase(
    fileName: String,
    index: Int,
    divData: JSONObject
): IntegrationTestCase {
    val actions = mutableListOf<JSONObject>()
    val expectedResults = mutableListOf<IntegrationTestCase.ExpectedResult>()
    var verificationFound = false

    getJSONArray("steps").toObjectList().forEach { step ->
        when (val type = step.getString("type")) {
            "div_action" -> {
                if (verificationFound) {
                    throw JSONException(
                        "div_action after verification is not supported by the Android integration runner"
                    )
                }
                actions.add(step.getJSONObject("action"))
            }

            "verify_variable", "verify_errors", "verify_view" -> {
                verificationFound = true
                expectedResults.add(step.parseExpectedResult())
            }

            else -> throw JSONException("Unknown integration step type: $type")
        }
    }

    var name = "$fileName Case $index"
    actions.forEach {
        name += ", ${it.getString("log_id")}"
    }

    return IntegrationTestCase(
        name = name,
        divData = divData,
        actions = actions,
        expectedResults = expectedResults
    )
}

private fun JSONObject.parseExpectedResult(): IntegrationTestCase.ExpectedResult {
    return when (val type = getString("type")) {
        "verify_variable" -> {
            val value = getJSONObject("value")
            IntegrationTestCase.ExpectedResult.Variable(
                name = getString("variable_name"),
                type = value.getString("type"),
                value = value.getVariableValue()
            )
        }

        "verify_errors" -> {
            val errors = getJSONArray("errors")
            IntegrationTestCase.ExpectedResult.Errors(
                List(errors.length()) { errors.getString(it) }
            )
        }

        "verify_view" -> {
            IntegrationTestCase.ExpectedResult.View(
                id = getString("id"),
                isShown = optBoolean("shown", true),
                text = if (has("text")) getString("text") else null,
                scopeId = if (has("scope_id")) getString("scope_id") else null,
            )
        }

        else -> throw JSONException("Unknown expected result type: $type")
    }
}

private fun JSONObject.getVariableValue(): Any {
    return when (val type = getString("type")) {
        "array" -> getJSONArray("value")
        "boolean" -> get("value")
        "color" -> Color.parse(getString("value"))
        "datetime" -> parseDateTime(getString("value"))
        "dict" -> getJSONObject("value")
        "integer" -> getLong("value")
        "number" -> getDouble("value")
        "string" -> getString("value")
        "url" -> getString("value")
        else -> throw IllegalAccessException("Unknown variable type: $type")
    }
}
