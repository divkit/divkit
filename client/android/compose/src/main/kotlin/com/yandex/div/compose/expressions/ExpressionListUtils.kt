package com.yandex.div.compose.expressions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.yandex.div.compose.utils.expressionResolver
import com.yandex.div.json.expressions.ConstantExpressionList
import com.yandex.div.json.expressions.ExpressionList

@Composable
internal fun <T : Any> ExpressionList<T>.observedValue(): List<T> {
    val expressionResolver = expressionResolver
    return when (this) {
        is ConstantExpressionList -> evaluate(expressionResolver)
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
