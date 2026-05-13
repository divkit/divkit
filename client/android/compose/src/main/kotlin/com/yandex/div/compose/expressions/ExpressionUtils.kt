package com.yandex.div.compose.expressions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.yandex.div.compose.utils.expressionResolver
import com.yandex.div.compose.utils.toColor
import com.yandex.div.json.expressions.Expression

@Composable
fun <T : Any> Expression<T>?.observedValue(defaultValue: T): T {
    val expressionResolver = expressionResolver
    return when (this) {
        null -> defaultValue
        is Expression.ConstantExpression -> evaluate(expressionResolver)
        else -> {
            val state = remember(this, expressionResolver) {
                mutableStateOf(defaultValue)
            }

            DisposableEffect(this, expressionResolver) {
                val disposable = observeAndGet(expressionResolver) { state.value = it }
                onDispose { disposable.close() }
            }

            state.value
        }
    }
}

@Composable
fun Expression<Double>?.observedFloatValue(defaultValue: Float): Float {
    return observedValue(defaultValue.toDouble()).toFloat()
}

@Composable
fun <T : Any> Expression<T>.observedValue(): T {
    val expressionResolver = expressionResolver
    return when (this) {
        is Expression.ConstantExpression -> evaluate(expressionResolver)
        else -> {
            val state = remember(this, expressionResolver) {
                mutableStateOf(evaluate(expressionResolver))
            }

            DisposableEffect(this, expressionResolver) {
                val disposable = observe(expressionResolver) { state.value = it }
                onDispose { disposable.close() }
            }

            state.value
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
            val state = remember(this, expressionResolver) {
                mutableStateOf(transform(evaluate(expressionResolver)))
            }

            DisposableEffect(this, expressionResolver) {
                val disposable = observe(expressionResolver) { state.value = transform(it) }
                onDispose { disposable.close() }
            }

            state.value
        }
    }
}
