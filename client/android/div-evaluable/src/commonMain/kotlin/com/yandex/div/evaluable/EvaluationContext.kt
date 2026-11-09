package com.yandex.div.evaluable

public class EvaluationContext(
    public val variableProvider: VariableProvider,
    public val storedValueProvider: StoredValueProvider,
    public val functionProvider: FunctionProvider,
    public val warningSender: WarningSender
) {
    public fun copy(variableProvider: VariableProvider): EvaluationContext {
        return EvaluationContext(
            variableProvider = variableProvider,
            storedValueProvider = storedValueProvider,
            functionProvider = functionProvider,
            warningSender = warningSender
        )
    }
}
