package com.yandex.div.compose.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.yandex.div.json.expressions.Expression

@Composable
internal fun <T : Any> Expression<T>?.observedValue(defaultValue: T): T {
    return when (this) {
        null -> defaultValue
        is Expression.ConstantExpression -> value
        else -> asState(defaultValue).value
    }
}

@Composable
internal fun <T : Any> Expression<T>.observedValue(): T {
    return when (this) {
        is Expression.ConstantExpression -> value
        else -> asState().value
    }
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
