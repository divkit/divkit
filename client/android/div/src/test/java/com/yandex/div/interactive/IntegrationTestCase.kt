package com.yandex.div.interactive

import com.yandex.div.core.expression.ExpressionTestCaseUtils.getVariableValue
import com.yandex.div.core.expression.ExpressionTestCaseUtils.type
import org.json.JSONObject

class IntegrationTestCase(
    val name: String,
    val divData: JSONObject,
    val actions: List<JSONObject>?,
    val expected: List<ExpectedResult>,
) {

    sealed interface ExpectedResult {

        class Variable(val name: String, json: JSONObject): ExpectedResult {
            val type: String = json.type
            val value = json.getVariableValue(type)
        }

        class Error(val message: String): ExpectedResult
    }

    override fun toString() = name
}
