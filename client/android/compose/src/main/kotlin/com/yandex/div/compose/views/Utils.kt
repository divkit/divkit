package com.yandex.div.compose.views

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.DivReporter
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivSizeUnit

internal fun Int.toColor(): Color {
    return Color(this)
}

internal fun Long.toDp(): Dp {
    return toFloat().dp
}

internal fun Double.toDp(): Dp {
    return toFloat().dp
}

@Composable
fun Float.toDp(unit: DivSizeUnit): Dp {
    val float = this
    return when (unit) {
        DivSizeUnit.DP -> float.dp
        DivSizeUnit.PX -> with(LocalDensity.current) { float.toDp() }
        else -> float.dp
    }
}

@Composable
fun Dp.toPx(): Float {
    return with(LocalDensity.current) {
        this@toPx.toPx()
    }
}

internal val divContext: DivContext
    @Composable
    get() = LocalContext.current as DivContext

internal val expressionResolver: ExpressionResolver
    @Composable
    get() = divContext.expressionResolver

internal val reporter: DivReporter
    @Composable
    get() = divContext.reporter

@Composable
internal fun <T : Any> Expression<T>.evaluate(): T {
    return evaluate(expressionResolver)
}
