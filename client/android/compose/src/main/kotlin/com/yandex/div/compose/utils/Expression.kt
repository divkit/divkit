package com.yandex.div.compose.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.yandex.div.json.expressions.ConstantExpressionList
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionList

@Composable
internal fun <T : Any> Expression<T>?.observedValue(defaultValue: T): T {
    return when (this) {
        null -> defaultValue
        is Expression.ConstantExpression -> evaluate(expressionResolver)
        else -> asState(defaultValue).value
    }
}

@Composable
internal fun Expression<Double>?.observedFloatValue(defaultValue: Float): Float {
    return observedValue(defaultValue.toDouble()).toFloat()
}

@Composable
internal fun <T : Any> Expression<T>.observedValue(): T {
    return when (this) {
        is Expression.ConstantExpression -> evaluate(expressionResolver)
        else -> asState().value
    }
}

@Composable
internal fun Expression<Int>.observedColorValue(): Color {
    return observedValue().toColor()
}

@Composable
internal fun Expression<Long>.observedIntValue(): Int {
    return observedValue().toInt()
}

@Composable
internal fun Expression<Double>.observedFloatValue(): Float {
    return observedValue().toFloat()
}

@Composable
private fun <T : Any> Expression<T>.asState(): State<T> {
    val resolver = expressionResolver
    val state = remember(this, resolver) { mutableStateOf(evaluate(resolver)) }

    DisposableEffect(this, resolver) {
        val disposable = observe(resolver) { state.value = it }
        onDispose { disposable.close() }
    }

    return state
}

@Composable
private fun <T : Any> Expression<T>.asState(defaultValue: T): State<T> {
    val resolver = expressionResolver
    val state = remember(this, resolver) { mutableStateOf(defaultValue) }

    DisposableEffect(this, resolver) {
        val disposable = observeAndGet(resolver) { state.value = it }
        onDispose { disposable.close() }
    }

    return state
}

@Composable
internal fun <T : Any> ExpressionList<T>.observedValue(): List<T> {
    return when (this) {
        is ConstantExpressionList -> evaluate(expressionResolver)
        else -> asState().value
    }
}

@Composable
private fun <T : Any> ExpressionList<T>.asState(): State<List<T>> {
    val resolver = expressionResolver
    val state = remember(this, resolver) { mutableStateOf(evaluate(resolver)) }

    DisposableEffect(this, resolver) {
        val disposable = observe(resolver) { state.value = it }
        onDispose { disposable.close() }
    }

    return state
}
