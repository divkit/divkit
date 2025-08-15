package com.yandex.div.evaluable

data class FunctionArgument(
    val type: EvaluableType,
    val isVariadic: Boolean = false
)
