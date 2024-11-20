package com.yandex.div.core.expression.local

import com.yandex.div.core.expression.variables.ConstantsProvider
import com.yandex.div.core.expression.variables.VariableAndConstantController
import com.yandex.div.core.expression.variables.VariableController
import com.yandex.div.evaluable.Evaluable
import com.yandex.div.evaluable.EvaluableType
import com.yandex.div.evaluable.EvaluationContext
import com.yandex.div.evaluable.Evaluator
import com.yandex.div.evaluable.ExpressionContext
import com.yandex.div.evaluable.Function
import com.yandex.div.evaluable.FunctionArgument

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
        val argConstants = mutableMapOf<String, Any>()
        argNames.forEachIndexed { index, name ->
            argConstants[name] = args[index]
        }
        val variableProvider = VariableAndConstantController(
            evaluationContext.variableProvider as VariableController,
            ConstantsProvider(argConstants),
        )
        val newContext = EvaluationContext(
            variableProvider,
            evaluationContext.storedValueProvider,
            evaluationContext.functionProvider,
            evaluationContext.warningSender
        )
        return Evaluator(newContext).eval(evaluable)
    }
}
