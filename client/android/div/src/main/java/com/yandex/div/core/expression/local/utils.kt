package com.yandex.div.core.expression.local

import com.yandex.div2.Div

internal val Div.needLocalRuntime get() =
    !value().run { variables.isNullOrEmpty() && variableTriggers.isNullOrEmpty() && functions.isNullOrEmpty() }
