package com.yandex.div.compose.utils

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yandex.div.compose.dagger.DivLocalComponent
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div2.DivBase
import com.yandex.div2.DivEdgeInsets
import com.yandex.div2.DivSizeUnit

@Composable
internal fun DivEdgeInsets?.observeInsets(): PaddingValues {
    val unit = this?.unit?.observedValue() ?: return PaddingValues.Zero

    val (start, end) = observeHorizontalInsets(unit = unit)
    return PaddingValues(
        start = start,
        end = end,
        top = top.observedValue().toDp(unit),
        bottom = bottom.observedValue().toDp(unit)
    )
}

@Composable
internal fun DivEdgeInsets?.observeHorizontalInsets(
    component: DivLocalComponent = LocalComponent.current,
): Pair<Dp, Dp> {
    val unit = this?.unit?.observedValue(component = component) ?: return ZeroPair
    return observeHorizontalInsets(unit, component)
}

@Composable
internal fun DivEdgeInsets?.observeVerticalInsets(
    component: DivLocalComponent = LocalComponent.current,
): Pair<Dp, Dp> {
    val unit = this?.unit?.observedValue(component = component) ?: return ZeroPair
    return Pair(
        top.observedValue(component = component).toDp(unit),
        bottom.observedValue(component = component).toDp(unit),
    )
}

@Composable
internal fun DivBase.observeHorizontalMarginsSum(): Dp =
    observeHorizontalMarginsSum(component = null)

@Composable
internal fun DivBase.observeHorizontalMarginsSum(component: DivLocalComponent?): Dp {
    val margins = margins ?: return 0.dp
    val localComponent = component ?: LocalComponent.current
    val unit = margins.unit.observedValue(component = localComponent)
    val (start, end) = margins.observeHorizontalInsets(unit, localComponent)
    return start + end
}

@Composable
internal fun DivBase.observeVerticalMarginsSum(): Dp = observeVerticalMarginsSum(component = null)

@Composable
internal fun DivBase.observeVerticalMarginsSum(component: DivLocalComponent?): Dp {
    val margins = margins ?: return 0.dp
    val localComponent = component ?: LocalComponent.current
    val unit = margins.unit.observedValue(component = localComponent)
    return margins.top.observedValue(component = localComponent).toDp(unit) +
        margins.bottom.observedValue(component = localComponent).toDp(unit)
}

@Composable
private fun DivEdgeInsets?.observeHorizontalInsets(
    unit: DivSizeUnit,
    component: DivLocalComponent = LocalComponent.current
): Pair<Dp, Dp> {
    this ?: return ZeroPair

    if (start != null || end != null) {
        return Pair(
            start?.observedValue(component = component)?.toDp(unit) ?: 0.dp,
            end?.observedValue(component = component)?.toDp(unit) ?: 0.dp
        )
    }

    val left = left.observedValue(component = component).toDp(unit)
    val right = right.observedValue(component = component).toDp(unit)
    return if (isRtlLayout) {
        Pair(right, left)
    } else {
        Pair(left, right)
    }
}

private val ZeroPair = Pair(0.dp, 0.dp)
