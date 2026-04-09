package com.yandex.div.compose.views.modifiers

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yandex.div.compose.utils.observedColorValue
import com.yandex.div.compose.utils.observedFloatValue
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.compose.utils.toDp
import com.yandex.div.compose.utils.toPx
import com.yandex.div2.DivBorder
import com.yandex.div2.DivStroke
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
    return borderStrokeWithShape(stroke, shape)
}

@Composable
private fun DivBorder.toShape(): Shape {
    val cornersRadius = cornersRadius
    val singleRadius = cornerRadius?.observedValue()
    return when {
        cornersRadius != null -> RoundedCornerShape(
            topStart = (cornersRadius.topLeft?.observedValue() ?: singleRadius ?: 0L).toDp(),
            topEnd = (cornersRadius.topRight?.observedValue() ?: singleRadius ?: 0L).toDp(),
            bottomStart = (cornersRadius.bottomLeft?.observedValue() ?: singleRadius ?: 0L).toDp(),
            bottomEnd = (cornersRadius.bottomRight?.observedValue() ?: singleRadius ?: 0L).toDp(),
        )
        singleRadius != null -> RoundedCornerShape(singleRadius.toDp())
        else -> RectangleShape
    }
}

@Composable
private fun Modifier.borderStrokeWithShape(stroke: DivStroke, shape: Shape): Modifier {
    val color = stroke.color.observedColorValue()
    val width = stroke.widthToDp()
    return when (stroke.style) {
        is DivStrokeStyle.Solid -> border(BorderStroke(width, color), shape)
        is DivStrokeStyle.Dashed -> drawBehindDashedBorder(color, width, shape)
    }
}

@Composable
private fun DivStroke.widthToDp(): Dp {
    return width.observedFloatValue().toDp(unit.observedValue())
}

@Composable
private fun Modifier.drawBehindDashedBorder(
    color: Color,
    width: Dp,
    shape: Shape
): Modifier {
    val density = LocalDensity.current
    val widthPx = width.toPx()
    val dashWidth = 6.dp.toPx()
    val gapWidth = 2.dp.toPx()
    return drawBehind {
        val outline = shape.createOutline(size, layoutDirection, density)
        drawOutline(
            outline = outline,
            color = color,
            style = Stroke(
                width = widthPx,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashWidth, gapWidth), 0f)
            )
        )
    }
}
