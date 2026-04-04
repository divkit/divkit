package com.yandex.div.compose.views.modifiers

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import com.yandex.div.compose.utils.observedFloatValue
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.compose.utils.toDp
import com.yandex.div.compose.utils.toPx
import com.yandex.div2.DivPivot
import com.yandex.div2.DivPivotFixed
import com.yandex.div2.DivTransform

@Composable
internal fun Modifier.transform(transform: DivTransform): Modifier {
    val rotation = transform.rotation?.observedFloatValue() ?: 0f

    val pivotX = transform.pivotX.observePivot()
    val pivotY = transform.pivotY.observePivot()

    return graphicsLayer {
        rotationZ = rotation
        transformOrigin = TransformOrigin(
            pivotFractionX = pivotX.toFraction(size.width),
            pivotFractionY = pivotY.toFraction(size.height),
        )
    }
}

private sealed class Pivot {
    class Fraction(val value: Float) : Pivot()
    class Fixed(val valuePx: Float) : Pivot()

    fun toFraction(sizeComponent: Float): Float = when (this) {
        is Fraction -> value
        is Fixed -> if (sizeComponent > 0f) valuePx / sizeComponent else 0.5f
    }
}

@Composable
private fun DivPivot.observePivot(): Pivot {
    return when (this) {
        is DivPivot.Percentage -> Pivot.Fraction(value.value.observedFloatValue() / 100f)
        is DivPivot.Fixed -> Pivot.Fixed(value.toPx())
    }
}

@Composable
private fun DivPivotFixed.toPx(): Float {
    val value = value?.observedValue()?.toFloat() ?: return 0f
    return value.toDp(unit.observedValue()).toPx()
}
