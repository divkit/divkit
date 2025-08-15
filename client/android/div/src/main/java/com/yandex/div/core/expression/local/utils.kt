package com.yandex.div.core.expression.local

import com.yandex.div.core.expression.ExpressionResolverImpl
import com.yandex.div.json.expressions.ExpressionResolver

internal val ExpressionResolver.asImpl get() = this as? ExpressionResolverImpl

internal val ExpressionResolver.variableController get() = asImpl?.variableController
