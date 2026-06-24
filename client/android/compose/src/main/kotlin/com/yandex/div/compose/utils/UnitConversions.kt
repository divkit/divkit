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
internal fun Float.toDp(unit: DivSizeUnit): Dp {
    return when (unit) {
        DivSizeUnit.DP, DivSizeUnit.SP -> dp
        DivSizeUnit.PX -> with(LocalDensity.current) { this@toDp.toDp() }
    }
}

@Composable
internal fun Long.toDp(unit: DivSizeUnit): Dp {
    return toFloat().toDp(unit)
}

@Composable
internal fun Float.toPx(unit: DivSizeUnit): Float {
    return when (unit) {
        DivSizeUnit.DP, DivSizeUnit.SP -> dp.toPx()
        DivSizeUnit.PX -> this
    }
}

@Composable
internal fun Long.toPx(unit: DivSizeUnit): Float {
    return toFloat().toPx(unit)
}

@Composable
internal fun Dp.toPx(): Float {
    return with(LocalDensity.current) {
        this@toPx.toPx()
    }
}

@Composable
internal fun Int.toTextUnit(unit: DivSizeUnit): TextUnit {
    return when (unit) {
        DivSizeUnit.SP -> sp
        DivSizeUnit.DP -> sp // DP and SP are the same at default font scale
        DivSizeUnit.PX -> with(LocalDensity.current) { this@toTextUnit.toSp() }
    }
}
