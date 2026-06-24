package com.yandex.div.compose.views.modifiers

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import com.yandex.div.compose.expressions.observedFloatValue
import com.yandex.div.compose.utils.observedPxValue
import com.yandex.div2.DivPivot
import com.yandex.div2.DivTransform

@Composable
internal fun Modifier.transform(transform: DivTransform): Modifier {
    val rotation = transform.rotation?.observedFloatValue() ?: 0f
    val pivotX = transform.pivotX.observedPivot()
    val pivotY = transform.pivotY.observedPivot()

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
private fun DivPivot.observedPivot(): Pivot {
    return when (this) {
        is DivPivot.Percentage -> Pivot.Fraction(value.value.observedFloatValue() / 100f)
        is DivPivot.Fixed -> Pivot.Fixed(value.value.observedPxValue(value.unit))
    }
}
