package com.yandex.div.core.expression

import com.yandex.div.core.expression.variables.wrapVariableValue
import com.yandex.div.data.Variable
import com.yandex.div.json.expressions.ExpressionResolver

internal val ExpressionResolver.asImpl get() = this as? ExpressionResolverImpl

internal fun Variable.getWrappedValue() = getValue().wrapVariableValue()
