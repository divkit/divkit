package com.yandex.div.compose.expressions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.yandex.div.compose.context.expressionResolver
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.utils.toColor
import com.yandex.div.json.expressions.Expression
import kotlin.reflect.KClass

@Composable
inline fun <reified T : Any> Expression<T>?.observedValue(defaultValue: T): T {
    return observedValue(defaultValue, T::class)
}

@PublishedApi
@Composable
internal fun <T : Any> Expression<T>?.observedValue(
    defaultValue: T,
    valueType: KClass<T>
): T {
    val expressionResolver = expressionResolver
    return when (this) {
        null -> defaultValue
        is Expression.ConstantExpression -> evaluate(expressionResolver)
        else -> cachedObservedValue(this, valueType)
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
inline fun <reified T : Any> Expression<T>.observedValue(): T {
    return observedValue(T::class)
}

@PublishedApi
@Composable
internal fun <T : Any> Expression<T>.observedValue(valueType: KClass<T>): T {
    val expressionResolver = expressionResolver
    return when (this) {
        is Expression.ConstantExpression -> evaluate(expressionResolver)
        else -> cachedObservedValue(this, valueType)
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
internal inline fun <reified T : Any, R> Expression<T>.observedValue(
    crossinline transform: (T) -> R
): R {
    val expressionResolver = expressionResolver
    return when (this) {
        is Expression.ConstantExpression ->
            remember(this, expressionResolver) {
                transform(evaluate(expressionResolver))
            }

        else -> transform(cachedObservedValue(this, T::class))
    }
}

@Composable
private fun <T : Any> cachedObservedValue(
    expression: Expression<T>,
    valueType: KClass<T>
): T {
    val cache = LocalComponent.current.expressionCache
    val ref = remember(expression, cache) {
        cache.getOrCreate(expression, valueType)
    }
    return ref.value
}
