package com.yandex.div.core.expression

import org.json.JSONArray
import org.json.JSONObject

data class ExpressionTestCase(
    val fileName: String,
    val expression: String,
    val variables: List<JSONObject>,
    val functions: List<JSONObject>,
    val platform: List<String>,
    val expectedType: String,
    val expectedValue: Any,
    val expectedWarnings: List<String>,
) {
    val description: String
        get() {
            val formattedExpression = if (expression.startsWith("@{")
                && expression.endsWith("}")
                && !expression.drop(2).contains("@{")) {
                expression.drop(2).dropLast(1)
            } else {
                expression
            }
            val result: String = when (expectedValue) {
                is String -> "'$expectedValue'"
                is JSONArray -> "<array>"
                is JSONObject -> "<dict>"
                is Exception -> "error"
                else -> expectedValue.toString()
            }
            val description = "$formattedExpression -> $result"
            val variablesHash = if (variables.isEmpty()) "" else ("variables hash " + variables.hashCode())
            val functionsHash = if (functions.isEmpty()) "" else ("functions hash " + functions.hashCode())
            if (variablesHash.isEmpty() && functionsHash.isEmpty()) {
                return description
            }
            return "$description ($variablesHash" +
                (if (variablesHash.isNotEmpty() && functionsHash.isNotEmpty()) ", " else "") + "$functionsHash)"
        }

    override fun toString(): String {
        return description
    }
}
