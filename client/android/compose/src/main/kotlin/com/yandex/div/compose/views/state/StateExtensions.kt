package com.yandex.div.compose.views.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import com.yandex.div.compose.views.expressionResolver
import com.yandex.div.json.expressions.Expression

@Composable
@JvmName("asStateNullable")
internal fun <T : Any> Expression<T>?.asState(defaultValue: T): State<T> {
    return this?.asState(defaultValue) ?: rememberUpdatedState(defaultValue)
}

@Composable
internal fun <T : Any> Expression<T>.asState(): State<T> {
    val resolver = expressionResolver
    val state = remember(this, resolver) { mutableStateOf(evaluate(resolver)) }

    DisposableEffect(this, resolver) {
        val disposable = observe(resolver) { state.value = it }
        onDispose { disposable.close() }
    }

    return state
}

@Composable
internal fun <T : Any> Expression<T>.asState(defaultValue: T): State<T> {
    val resolver = expressionResolver
    val state = remember(this, resolver) { mutableStateOf(defaultValue) }

    DisposableEffect(this, resolver) {
        val disposable = observeAndGet(resolver) { state.value = it }
        onDispose { disposable.close() }
    }

    return state
}

@Composable
internal fun <T : Any> Expression<T>?.observeAsValue(defaultValue: T): T {
    return asState(defaultValue).value
}

@Composable
internal fun <T : Any> Expression<T>.observeAsValue(): T {
    return asState().value
}
