package com.yandex.div.core.expression.local

import com.yandex.div.data.Variable
import com.yandex.div2.Div
import com.yandex.div2.DivFunction
import com.yandex.div2.DivTrigger

internal val Div.needLocalRuntime get() =
    !value().run { variables.isNullOrEmpty() && variableTriggers.isNullOrEmpty() && functions.isNullOrEmpty() }

internal fun needLocalRuntime(
    variables: List<Variable>?,
    variableTriggers: List<DivTrigger>?,
    functions: List<DivFunction>?
) = !(variables.isNullOrEmpty() && variableTriggers.isNullOrEmpty() && functions.isNullOrEmpty())
