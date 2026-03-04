package com.yandex.div.compose.views

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yandex.div.compose.DivContext
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver

internal fun Int.toColor(): Color {
    return Color(this)
}

internal fun Long.toDp(): Dp {
    return toFloat().dp
}

internal val divContext: DivContext
    @Composable
    get() = LocalContext.current as DivContext

internal val expressionResolver: ExpressionResolver
    @Composable
    get() = divContext.expressionResolver

@Composable
internal fun <T : Any> Expression<T>.evaluate(): T {
    return evaluate(expressionResolver)
}
