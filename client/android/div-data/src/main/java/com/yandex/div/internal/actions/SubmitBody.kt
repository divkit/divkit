package com.yandex.div.internal.actions

import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.evaluable.MissingVariableException
import com.yandex.div.internal.variables.name
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivVariable
import org.json.JSONObject

@InternalApi
fun createSubmitBody(
    variables: List<DivVariable>?,
    expressionResolver: ExpressionResolver,
    reportError: (Exception) -> Unit,
): String {
    if (variables.isNullOrEmpty()) {
        return ""
    }

    val body = JSONObject()
    variables.forEach {
        val name = it.name
        val variable = expressionResolver.getVariable(name)
        if (variable == null) {
            reportError(MissingVariableException(name))
        } else {
            body.put(name, variable.getValue())
        }
    }
    return body.toString()
}
