package com.yandex.div.model.expression

import kotlin.jvm.JvmField

/**
 * A container for collection of [Expression]s.
 *
 * Models only hold expression lists. The `:runtime` module evaluates them the same way as [Expression].
 */
public interface ExpressionList<T : Any>

public class ConstantExpressionList<T : Any>(
    @JvmField public val values: List<T>,
) : ExpressionList<T> {

    override fun hashCode(): Int {
        return values.hashCode() * 16
    }

    override fun equals(other: Any?): Boolean {
        return other is ConstantExpressionList<*> && values == other.values
    }
}
