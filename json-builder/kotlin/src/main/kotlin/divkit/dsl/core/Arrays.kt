package divkit.dsl.core

import divkit.dsl.Color

/**
 * Denotes an element of an arbitrary DivKit array.
 */
sealed interface ArrayElement<T>

/**
 * [ArrayElement] that resolves as a literal value.
 */
data class LiteralArrayElement<T> internal constructor(
    val value: T,
) : ArrayElement<T>

/**
 * [ArrayElement] that resolves as an expression.
 */
data class ExpressionArrayElement<T> internal constructor(
    val expression: String,
) : ArrayElement<T>

/**
 * [ArrayElement] that resolves as a literal value.
 */
fun <T> valueArrayElement(value: T): ArrayElement<T> = LiteralArrayElement(value)

/**
 * [ArrayElement] that resolves as an expression.
 */
fun <T> expressionArrayElement(expression: String): ArrayElement<T> = ExpressionArrayElement(expression)

internal fun ArrayElement<out Any>.serialize(): Any {
    return when (this) {
        is LiteralArrayElement -> value
        is ExpressionArrayElement -> expression
    }
}

/**
 * Creates [ArrayElement] around [Color] hex.
 */
fun colorArrayElement(argb: String): ArrayElement<Color> =
    LiteralArrayElement(Color(argb))