package com.yandex.div.core.expression

import com.yandex.div.json.expressions.ExpressionResolver

internal val ExpressionResolver.asImpl get() = this as? ExpressionResolverImpl
