package com.yandex.div.json.expressions

fun <T : Any> Expression<T>.isConstant(): Boolean {
    return this is Expression.ConstantExpression<T>
}

fun <T : Any> Expression<T>?.isConstantOrNull(): Boolean {
    return this == null || this.isConstant()
}

fun <T : Any> ExpressionList<T>.isConstant(): Boolean {
    return this is ConstantExpressionList<T>
}

fun <T : Any> ExpressionList<T>?.isConstantOrNull(): Boolean {
    return this == null || this.isConstant()
}

fun <T : Any> Expression<T>?.equalsToConstant(other: Expression<T>?): Boolean {
    if (this == null && other == null) {
        return true
    }

    return this != null && this.isConstant()
        && other != null && other.isConstant()
        && this.rawValue == other.rawValue
}

fun <T : Any> ExpressionList<T>?.equalsToConstant(other: ExpressionList<T>?): Boolean {
    if (this == null && other == null) {
        return true
    }

    return this != null && this is ConstantExpressionList<T>
        && other != null && other is ConstantExpressionList<T>
        && this.values == other.values
}
