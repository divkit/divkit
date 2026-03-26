package com.yandex.div.internal.expressions

import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.evaluable.Evaluable
import com.yandex.div.evaluable.EvaluableType
import com.yandex.div.evaluable.EvaluationContext
import com.yandex.div.evaluable.Evaluator
import com.yandex.div.evaluable.ExpressionContext
import com.yandex.div.evaluable.Function
import com.yandex.div.evaluable.FunctionArgument
import com.yandex.div.evaluable.VariableProvider

@InternalApi
class LocalFunction(
    override val name: String,
    override val declaredArgs: List<FunctionArgument>,
    override val resultType: EvaluableType,
    private val argNames: List<String>,
    body: String,
) : Function() {

    override val isPure = false

    private val evaluable = Evaluable.lazy(body)

    override fun evaluate(
        evaluationContext: EvaluationContext,
        expressionContext: ExpressionContext,
        args: List<Any>
    ): Any {
        val argsMap = mutableMapOf<String, Any>()
        argNames.forEachIndexed { index, name ->
            argsMap[name] = args[index]
        }
        val newContext = EvaluationContext(
            variableProvider = VariableProviderWrapper(
                values = argsMap,
                wrappedProvider = evaluationContext.variableProvider
            ),
            storedValueProvider = evaluationContext.storedValueProvider,
            functionProvider = evaluationContext.functionProvider,
            warningSender = evaluationContext.warningSender
        )
        return Evaluator(newContext).eval(evaluable)
    }
}

private class VariableProviderWrapper(
    private val values: Map<String, Any>,
    private val wrappedProvider: VariableProvider
) : VariableProvider {
    override fun get(name: String): Any? {
        return values[name] ?: wrappedProvider.get(name)
    }
}
