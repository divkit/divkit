package com.yandex.div.compose.expressions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.yandex.div.compose.context.expressionResolver
import com.yandex.div.core.Disposable
import com.yandex.div.json.expressions.ConstantExpressionList
import com.yandex.div.json.expressions.ExpressionList
import com.yandex.div.json.expressions.ExpressionResolver

@Composable
internal fun <T : Any> ExpressionList<T>.observedValue(): List<T> {
    val expressionResolver = expressionResolver
    return when (this) {
        is ConstantExpressionList -> evaluate(expressionResolver)
        else -> {
            val observer = remember(this, expressionResolver) {
                ExpressionListObserver(
                    expressionList = this,
                    expressionResolver = expressionResolver
                )
            }
            observer.value
        }
    }
}

private class ExpressionListObserver<T : Any>(
    private val expressionList: ExpressionList<T>,
    private val expressionResolver: ExpressionResolver
) : RememberObserver {
    private val state = mutableStateOf(expressionList.evaluate(expressionResolver))

    private var subscription: Disposable? = null

    val value: List<T> get() = state.value

    override fun onRemembered() {
        subscription = expressionList.observe(expressionResolver) { state.value = it }
    }

    override fun onForgotten() {
        subscription?.close()
        subscription = null
    }

    override fun onAbandoned() = Unit
}
