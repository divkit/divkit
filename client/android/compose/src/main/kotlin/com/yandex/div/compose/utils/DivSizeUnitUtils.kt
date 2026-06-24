package com.yandex.div.compose.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import com.yandex.div.compose.expressions.observedFloatValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivSizeUnit

@Composable
@JvmName("observedDpValueDouble")
internal fun Expression<Double>.observedDpValue(unit: Expression<DivSizeUnit>): Dp {
    return observedFloatValue().toDp(unit.observedValue())
}

@Composable
@JvmName("observedDpValueLong")
internal fun Expression<Long>.observedDpValue(unit: Expression<DivSizeUnit>): Dp {
    return observedValue().toDp(unit.observedValue())
}

@Composable
@JvmName("observedPxValueDouble")
internal fun Expression<Double>.observedPxValue(unit: Expression<DivSizeUnit>): Float {
    return observedFloatValue().toPx(unit.observedValue())
}

@Composable
@JvmName("observedPxValueLong")
internal fun Expression<Long>.observedPxValue(unit: Expression<DivSizeUnit>): Float {
    return observedValue().toPx(unit.observedValue())
}
