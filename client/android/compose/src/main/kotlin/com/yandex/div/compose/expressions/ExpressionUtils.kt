package com.yandex.div.compose.expressions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.yandex.div.compose.context.expressionResolver
import com.yandex.div.compose.utils.toColor
import com.yandex.div.core.Disposable
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver

@Composable
fun <T : Any> Expression<T>?.observedValue(defaultValue: T): T {
    val expressionResolver = expressionResolver
    return when (this) {
        null -> defaultValue
        is Expression.ConstantExpression -> evaluate(expressionResolver)
        else -> {
            val observer = remember(this, expressionResolver) {
                ExpressionObserver(
                    expression = this,
                    expressionResolver = expressionResolver,
                    transform = { it }
                )
            }
            observer.value
        }
    }
}

@Composable
fun Expression<Double>?.observedFloatValue(defaultValue: Float): Float {
    return observedValue(defaultValue.toDouble()).toFloat()
}

@Composable
fun Expression<Long>?.observedIntValue(defaultValue: Int): Int {
    return observedValue(defaultValue.toLong()).toInt()
}

@Composable
fun <T : Any> Expression<T>.observedValue(): T {
    val expressionResolver = expressionResolver
    return when (this) {
        is Expression.ConstantExpression -> evaluate(expressionResolver)
        else -> {
            val observer = remember(this, expressionResolver) {
                ExpressionObserver(
                    expression = this,
                    expressionResolver = expressionResolver,
                    transform = { it }
                )
            }
            observer.value
        }
    }
}

@Composable
fun Expression<Int>.observedColorValue(): Color {
    return observedValue().toColor()
}

@Composable
fun Expression<Long>.observedIntValue(): Int {
    return observedValue().toInt()
}

@Composable
fun Expression<Double>.observedFloatValue(): Float {
    return observedValue().toFloat()
}

@Composable
internal fun <T : Any, R> Expression<T>.observedValue(transform: (T) -> R): R {
    val expressionResolver = expressionResolver
    return when (this) {
        is Expression.ConstantExpression ->
            remember(this, expressionResolver) {
                transform(evaluate(expressionResolver))
            }

        else -> {
            val observer = remember(this, expressionResolver) {
                ExpressionObserver(
                    expression = this,
                    expressionResolver = expressionResolver,
                    transform = transform
                )
            }
            observer.value
        }
    }
}

private class ExpressionObserver<T : Any, R>(
    private val expression: Expression<T>,
    private val expressionResolver: ExpressionResolver,
    private val transform: (T) -> R
) : RememberObserver {
    private val state = mutableStateOf(transform(expression.evaluate(expressionResolver)))

    private var subscription: Disposable? = null

    val value: R get() = state.value

    override fun onRemembered() {
        subscription = expression.observe(expressionResolver) { state.value = transform(it) }
    }

    override fun onForgotten() {
        subscription?.close()
        subscription = null
    }

    override fun onAbandoned() = Unit
}
