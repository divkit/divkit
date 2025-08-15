package com.yandex.div.interactive

import com.yandex.div.core.expression.ExpressionTestCaseUtils.getVariableValue
import com.yandex.div.core.expression.ExpressionTestCaseUtils.type
import com.yandex.div2.DivAction
import com.yandex.div2.DivData
import org.json.JSONObject

class IntegrationTestCase(
    val name: String,
    val divData: DivData,
    val actions: List<DivAction>?,
    val expected: List<ExpectedResult>,
    val logger: IntegrationTestLogger,
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
