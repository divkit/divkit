package com.yandex.div.compose.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import com.yandex.div2.DivFixedSize
import com.yandex.div2.DivSize

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
internal fun DivFixedSize.observedValue(): Dp {
    return value.observedValue().toDp(unit.observedValue())
}
