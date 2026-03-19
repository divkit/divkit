package com.yandex.div.compose.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
