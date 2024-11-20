package com.yandex.div.core.util

import com.yandex.div.evaluable.EvaluableType
import com.yandex.div.evaluable.FunctionArgument
import com.yandex.div.core.expression.local.LocalFunction
import com.yandex.div2.DivEvaluableType
import com.yandex.div2.DivFunction

internal fun List<DivFunction>.toLocalFunctions(): List<LocalFunction> {
    return map { divFunction ->
        val argNames = mutableListOf<String>()
        val argTypes = mutableListOf<FunctionArgument>()
        divFunction.arguments.forEach {
            argNames.add(it.name)
            argTypes.add(FunctionArgument(it.type.toEvaluableType()))
        }
        LocalFunction(
            divFunction.name,
            argTypes,
            divFunction.returnType.toEvaluableType(),
            argNames,
            divFunction.body,
        )
    }
}

private fun DivEvaluableType.toEvaluableType() = when (this) {
    DivEvaluableType.STRING -> EvaluableType.STRING
    DivEvaluableType.INTEGER -> EvaluableType.INTEGER
    DivEvaluableType.NUMBER -> EvaluableType.NUMBER
    DivEvaluableType.BOOLEAN -> EvaluableType.BOOLEAN
    DivEvaluableType.DATETIME -> EvaluableType.DATETIME
    DivEvaluableType.COLOR -> EvaluableType.COLOR
    DivEvaluableType.URL -> EvaluableType.URL
    DivEvaluableType.DICT -> EvaluableType.DICT
    DivEvaluableType.ARRAY -> EvaluableType.ARRAY
}
