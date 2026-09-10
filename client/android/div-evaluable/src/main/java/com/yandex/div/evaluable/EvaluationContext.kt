package com.yandex.div.evaluable

class EvaluationContext(
    val variableProvider: VariableProvider,
    val storedValueProvider: StoredValueProvider,
    val functionProvider: FunctionProvider,
    val warningSender: WarningSender
) {
    fun copy(variableProvider: VariableProvider): EvaluationContext {
        return EvaluationContext(
            variableProvider = variableProvider,
            storedValueProvider = storedValueProvider,
            functionProvider = functionProvider,
            warningSender = warningSender
        )
    }
}
