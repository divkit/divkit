package com.yandex.div.compose.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import com.yandex.div.compose.expressions.observedFloatValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div2.DivDimension

@Composable
internal fun DivDimension.observedValue(): Dp {
    return value.observedFloatValue().toDp(unit.observedValue())
}
