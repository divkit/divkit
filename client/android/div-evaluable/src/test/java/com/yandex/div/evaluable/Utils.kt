package com.yandex.div.evaluable

import org.junit.Assert

internal fun <T> withEvaluator(
    variableProvider: VariableProvider,
    functionProvider: FunctionProvider,
    warningsValidator: (List<String>) -> Unit = { Assert.assertTrue(it.isEmpty()) },
    block: Evaluator.() -> T
) = run {
    val warnings = mutableListOf<String>()
    val evaluator = Evaluator(variableProvider, functionProvider) { warning, _ ->
        warnings.add(warning)
    }

    block(evaluator).also {
        warningsValidator(warnings)
    }
}
