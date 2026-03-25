package com.yandex.div.compose.utils

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yandex.div2.DivBase
import com.yandex.div2.DivEdgeInsets
import com.yandex.div2.DivSizeUnit

@Composable
internal fun DivEdgeInsets?.observeInsets(): PaddingValues {
    val unit = this?.unit?.observedValue() ?: return PaddingValues(0.dp)

    val (start, end) = observeHorizontalInsets(unit = unit)
    return PaddingValues(
        start = start,
        end = end,
        top = top.observedValue().toDp(unit),
        bottom = bottom.observedValue().toDp(unit)
    )
}

@Composable
internal fun DivEdgeInsets?.observeHorizontalInsets(): Pair<Dp, Dp> {
    val unit = this?.unit?.observedValue() ?: return Pair(0.dp, 0.dp)
    return this.observeHorizontalInsets(unit)
}

@Composable
internal fun DivEdgeInsets?.observeVerticalInsets(): Pair<Dp, Dp> {
    val unit = this?.unit?.observedValue() ?: return Pair(0.dp, 0.dp)
    return Pair(top.observedValue().toDp(unit), bottom.observedValue().toDp(unit))
}

@Composable
internal fun DivBase.observeHorizontalMarginsSum(): Dp {
    val margins = margins ?: return 0.dp
    val (start, end) = margins.observeHorizontalInsets()
    return start + end
}

@Composable
internal fun DivBase.observeVerticalMarginsSum(): Dp {
    val margins = margins ?: return 0.dp
    val unit = margins.unit.observedValue()
    return margins.top.observedValue().toDp(unit) + margins.bottom.observedValue().toDp(unit)
}

@Composable
private fun DivEdgeInsets?.observeHorizontalInsets(unit: DivSizeUnit): Pair<Dp, Dp> {
    this ?: return Pair(0.dp, 0.dp)

    if (start == null && end == null) {
        return Pair(left.observedValue().toDp(unit), right.observedValue().toDp(unit))
    }
    return Pair(start?.observedValue()?.toDp(unit) ?: 0.dp, end?.observedValue()?.toDp(unit) ?: 0.dp)
}