package com.yandex.div.model.expression

import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Expression represents a string with special placeholders that may reference to
 * variables or operations above set of variables. For example:
 * "Profile: @{name} (@{team})"
 * can be evaluated into "Profile: John (Design)".
 *
 * Models only hold expressions. The `:runtime` module evaluates them with `evaluate`, `observe`
 * and `observeAndGet` extensions over `ExpressionResolver` from `com.yandex.div.runtime.expression`.
 */
public abstract class Expression<T : Any> {

    /**
     * Value of expression is mutable so we cannot use it in equals/hashcode
     * instead we'll use its raw value.
     */
    public abstract val rawValue: Any

    override fun equals(other: Any?): Boolean {
        if (other !is Expression<*>) {
            return false
        }

        return rawValue == other.rawValue
    }

    override fun hashCode(): Int {
        return rawValue.hashCode() * 16
    }

    public companion object {

        @JvmStatic
        public fun <T : Any> constant(value: T): Expression<T> = ConstantExpression(value)
    }
}

public class ConstantExpression<T : Any>(
    @JvmField public val value: T
) : Expression<T>() {

    override val rawValue: Any
        get() = value
}
