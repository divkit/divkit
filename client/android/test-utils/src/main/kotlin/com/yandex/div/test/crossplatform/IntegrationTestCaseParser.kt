package com.yandex.div.test.crossplatform

import com.yandex.div.evaluable.types.Color
import org.json.JSONException
import org.json.JSONObject

object IntegrationTestCaseParser {

    fun parseCases(
        fileName: String,
        jsonString: String
    ): List<ParsingResult<IntegrationTestCase>> {
        val json = JSONObject(jsonString)
        return json.getJSONArray("cases")
            .toObjectList()
            .mapIndexedNotNull { index, jsonObject ->
                if (!jsonObject.isForAndroid) {
                    return@mapIndexedNotNull null
                }
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
    val actions = optJSONArray("div_actions").toObjectList()
    var name = "$fileName Case $index"
    actions.forEach {
        name += ", ${it.getString("log_id")}"
    }

    return IntegrationTestCase(
        name = name,
        divData = divData,
        actions = actions,
        expectedResults = getJSONArray("expected")
            .toObjectList()
            .map { it.parseExpectedResult() }
    )
}

private fun JSONObject.parseExpectedResult(): IntegrationTestCase.ExpectedResult {
    return when (val type = getString("type")) {
        "variable" -> {
            val value = getJSONObject("value")
            IntegrationTestCase.ExpectedResult.Variable(
                name = getString("variable_name"),
                type = value.getString("type"),
                value = value.getVariableValue()
            )
        }

        "error" -> IntegrationTestCase.ExpectedResult.Error(getString("value"))
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
