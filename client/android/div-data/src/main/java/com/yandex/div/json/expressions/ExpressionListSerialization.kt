package com.yandex.div.json.expressions

import com.yandex.div.core.annotations.InternalApi

/** Returns unevaluated expressions for serializers in separate modules. */
@InternalApi
fun <T : Any> ExpressionList<T>.getRawExpressions(): List<Expression<T>> = when (this) {
    is ConstantExpressionList -> values.map { Expression.constant(it) }
    is MutableExpressionList -> getExpressions()
}
