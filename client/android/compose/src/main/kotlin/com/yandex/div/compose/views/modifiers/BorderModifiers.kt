package com.yandex.div.compose.views.modifiers

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.yandex.div.compose.expressions.observedColorValue
import com.yandex.div.compose.expressions.observedFloatValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.observedDpValue
import com.yandex.div.compose.utils.observedRoundedCornerShape
import com.yandex.div.compose.utils.toDp
import com.yandex.div.compose.utils.toPx
import com.yandex.div2.DivBorder
import com.yandex.div2.DivShadow
import com.yandex.div2.DivStrokeStyle

@Composable
internal fun Modifier.borderClip(data: DivBorder): Modifier {
    val shape = data.toShape()
    return if (shape == RectangleShape) this else clip(shape)
}

@Composable
internal fun Modifier.borderStroke(data: DivBorder): Modifier {
    val stroke = data.stroke ?: return this
    val shape = data.toShape()
    val color = stroke.color.observedColorValue()
    val width = stroke.width.observedDpValue(stroke.unit)
    return when (stroke.style) {
        is DivStrokeStyle.Solid -> border(BorderStroke(width, color), shape)
        is DivStrokeStyle.Dashed -> dashedBorder(color, width, shape)
    }
}

@Composable
internal fun Modifier.borderShadow(data: DivBorder, contentAlpha: Float): Modifier {
    if (contentAlpha <= 0f) return this
    if (!data.hasShadow.observedValue()) return this
    return shadow(data.shadow, data.toShape(), contentAlpha)
}

@Composable
private fun DivBorder.toShape(): Shape {
    return observedRoundedCornerShape(cornerRadius, cornersRadius) ?: RectangleShape
}

@Composable
private fun Modifier.dashedBorder(
    color: Color,
    width: Dp,
    shape: Shape
): Modifier {
    val density = LocalDensity.current
    val dashWidth = 6.dp.toPx()
    val gapWidth = 2.dp.toPx()
    val stroke = Stroke(
        width = width.toPx(),
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashWidth, gapWidth), 0f)
    )
    return drawWithCache {
        val outline = shape.createOutline(size, layoutDirection, density)
        onDrawBehind {
            drawOutline(
                outline = outline,
                color = color,
                style = stroke
            )
        }
    }
}

@Composable
private fun Modifier.shadow(
    shadow: DivShadow?,
    shape: Shape,
    contentAlpha: Float
): Modifier {
    val alpha = (shadow?.alpha?.observedFloatValue() ?: DEFAULT_SHADOW_ALPHA) * contentAlpha
    if (alpha <= 0f) return this

    val color = shadow?.color?.observedColorValue() ?: DEFAULT_SHADOW_COLOR
    val radius = shadow?.blur?.observedValue()?.toDp() ?: DEFAULT_SHADOW_ELEVATION
    val offsetX = shadow?.offset?.x?.observedDpValue() ?: DEFAULT_SHADOW_OFFSET_X
    val offsetY = shadow?.offset?.y?.observedDpValue() ?: DEFAULT_SHADOW_OFFSET_Y

    return dropShadow(
        shape = shape,
        shadow = Shadow(
            radius = radius,
            color = color.copy(alpha = alpha),
            offset = DpOffset(offsetX, offsetY),
        )
    )
}

private val DEFAULT_SHADOW_ELEVATION = 3.dp
private val DEFAULT_SHADOW_OFFSET_X = 0.dp
private val DEFAULT_SHADOW_OFFSET_Y = 0.5.dp
private const val DEFAULT_SHADOW_ALPHA = 0.14f
private val DEFAULT_SHADOW_COLOR = Color.Black
