package com.yandex.div.evaluable

import org.junit.Assert.assertTrue

fun <T> withEvaluator(
    evaluationContext: EvaluationContext,
    block: Evaluator.() -> T
) = run {
    val warnings = mutableListOf<String>()
    val evaluator = Evaluator(
        EvaluationContext(
            variableProvider = evaluationContext.variableProvider,
            storedValueProvider = evaluationContext.storedValueProvider,
            functionProvider = evaluationContext.functionProvider,
            warningSender = { expressionContext, message ->
                warnings.add(message)
                evaluationContext.warningSender.send(expressionContext, message)
            }
        )
    )

    block(evaluator).also {
        assertTrue(warnings.isEmpty())
    }
}
