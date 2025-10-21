package divkit.dsl.expression

import divkit.dsl.core.ExpressionProperty
import divkit.dsl.core.expression

/**
 * Compiles expression into Divan's [ExpressionProperty].
 */
fun <T : Any> Expression<*>.divanExpression(): ExpressionProperty<T> = expression(compile())

/**
 * Compiles expression into Divan's [ExpressionProperty].
 */
@JvmName("divanExpressionWrapper")
fun <T : Any> divanExpression(expression: Expression<*>): ExpressionProperty<T> = expression(expression.compile())
