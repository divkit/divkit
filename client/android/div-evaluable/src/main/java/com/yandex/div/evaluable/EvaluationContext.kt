package com.yandex.div.evaluable

class EvaluationContext(
    val variableProvider: VariableProvider,
    val storedValueProvider: StoredValueProvider,
    val functionProvider: FunctionProvider,
    val warningSender: WarningSender
)
