package com.yandex.div.core.expression.local

import com.yandex.div.core.expression.ExpressionResolverImpl
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div

internal val Div.needLocalRuntime get() =
    !value().run { variables.isNullOrEmpty() && variableTriggers.isNullOrEmpty() && functions.isNullOrEmpty() }

internal val ExpressionResolver.asImpl get() = this as? ExpressionResolverImpl

internal val ExpressionResolver.variableController get() = asImpl?.variableController
