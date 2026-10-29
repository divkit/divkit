package com.yandex.div.compose.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import com.yandex.div.compose.dagger.DivLocalComponent
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div2.DivFixedSize
import com.yandex.div2.DivSize
import com.yandex.div2.DivSizeUnitValue

internal val DivSize.isWrapContent: Boolean
    get() = this is DivSize.WrapContent

internal val DivSize.isMatchParent: Boolean
    get() = this is DivSize.MatchParent

@Composable
internal fun DivSize.observeIsConstrained(): Boolean = when (this) {
    is DivSize.WrapContent -> value.constrained?.observedValue() == true
    is DivSize.Fixed, is DivSize.MatchParent -> true
}

@Composable
internal fun DivFixedSize.observedValue(
    component: DivLocalComponent = LocalComponent.current,
): Dp {
    val value = value.observedValue(component = component)
    return value.toDp(unit.observedValue(component = component))
}

@Composable
internal fun DivFixedSize.observedPxValue(
    component: DivLocalComponent = LocalComponent.current,
): Float {
    val value = value.observedValue(component = component)
    return value.toPx(unit.observedValue(component = component))
}

@Composable
internal fun DivSizeUnitValue.observedValue(): Dp {
    return value.observedDpValue(unit)
}
