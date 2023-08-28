package com.yandex.div.core.expression

import com.yandex.div.evaluable.Evaluator
import com.yandex.div.evaluable.FunctionProvider
import com.yandex.div.evaluable.VariableProvider

internal class ExpressionEvaluatorFactory(private val functionProvider: FunctionProvider) {
    fun create(variableProvider: VariableProvider, onWarning: (Throwable) -> Unit) =
        Evaluator(variableProvider, functionProvider) { warning, evaluable ->
            onWarning(Throwable("Warning occurred while evaluating '${evaluable.rawExpr}': $warning"))
        }
}