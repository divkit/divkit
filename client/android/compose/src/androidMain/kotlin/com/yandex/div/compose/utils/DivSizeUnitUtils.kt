package com.yandex.div.compose.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import com.yandex.div.compose.di.DivLocalComponent
import com.yandex.div.compose.di.LocalComponent
import com.yandex.div.compose.expressions.observedFloatValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivSizeUnit

@Composable
internal fun Expression<Long>.observedDpValue(): Dp {
    return observedValue().toDp()
}

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
internal fun Expression<Double>.observedPxValue(
    unit: Expression<DivSizeUnit>,
    component: DivLocalComponent = LocalComponent.current,
): Float {
    return observedValue(component).toFloat().toPx(unit.observedValue(component))
}

@Composable
@JvmName("observedPxValueLong")
internal fun Expression<Long>.observedPxValue(
    unit: Expression<DivSizeUnit>,
    component: DivLocalComponent = LocalComponent.current,
): Float {
    return observedValue(component).toPx(unit.observedValue(component))
}

@Composable
internal fun Expression<Long>.observedPxValue(
    unit: DivSizeUnit,
    component: DivLocalComponent = LocalComponent.current,
): Float {
    return observedValue(component).toPx(unit)
}
