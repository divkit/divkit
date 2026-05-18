package com.yandex.div.compose.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        DivSizeUnit.DP, DivSizeUnit.SP -> float.dp
        DivSizeUnit.PX -> with(LocalDensity.current) { float.toDp() }
    }
}

@Composable
fun Long.toDp(unit: DivSizeUnit): Dp {
    return toFloat().toDp(unit)
}

@Composable
fun Dp.toPx(): Float {
    return with(LocalDensity.current) {
        this@toPx.toPx()
    }
}

internal fun Int.toTextUnit(unit: DivSizeUnit, density: Density): TextUnit {
    return when (unit) {
        DivSizeUnit.SP -> this.sp
        DivSizeUnit.DP -> this.sp // DP and SP are the same at default font scale
        DivSizeUnit.PX -> with(density) { this@toTextUnit.toDp().toSp() }
    }
}
