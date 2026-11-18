package com.yandex.div.compose.views.container

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import com.yandex.div.compose.expressions.observedColorValue
import com.yandex.div.compose.utils.observedDpValue
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.compose.utils.reportError
import com.yandex.div.compose.views.modifiers.padding
import com.yandex.div2.DivContainer
import com.yandex.div2.DivDrawable
import com.yandex.div2.DivShape
import com.yandex.div2.DivStroke

@Composable
internal fun ContainerSeparator(
    separator: DivContainer.Separator,
    modifier: Modifier = Modifier
) {
    when (val style = separator.style) {
        is DivDrawable.Shape -> {
            val modifier = modifier.padding(separator.margins)
            when (val shape = style.value.shape) {
                is DivShape.RoundedRectangle -> {
                    val rect = shape.value
                    val color = (rect.backgroundColor ?: style.value.color)?.observedColorValue()
                    if (color == null) {
                        reportError("Separator color not defined")
                        return
                    }
                    val radius = rect.cornerRadius.observedValue()
                    val outline = remember(radius) { RoundedCornerShape(radius) }
                    Spacer(
                        modifier = modifier
                            .width(rect.itemWidth.observedValue())
                            .height(rect.itemHeight.observedValue())
                            .background(color, outline)
                            .separatorStroke(rect.stroke ?: style.value.stroke, outline)
                    )
                }

                is DivShape.Circle -> {
                    val circle = shape.value
                    val color = (circle.backgroundColor ?: style.value.color)?.observedColorValue()
                    if (color == null) {
                        reportError("Separator color not defined")
                        return
                    }
                    Spacer(
                        modifier = modifier
                            .size(circle.radius.observedValue() * 2)
                            .background(color, CircleShape)
                            .circleSeparatorStroke(circle.stroke ?: style.value.stroke)
                    )
                }
            }
        }
    }
}

@Composable
private fun Modifier.separatorStroke(stroke: DivStroke?, shape: Shape): Modifier {
    val border = stroke?.observeSeparatorStroke() ?: return this
    return border(border, shape)
}

@Composable
private fun Modifier.circleSeparatorStroke(stroke: DivStroke?): Modifier {
    val border = stroke?.observeSeparatorStroke() ?: return this
    return drawWithCache {
        val style = Stroke(border.width.toPx())
        val radius = size.minDimension / 2
        onDrawBehind {
            if (radius <= 0f) return@onDrawBehind
            // Circle drawables center the stroke on the contour, including the part outside the bounds.
            if (style.width >= radius * 2) {
                drawCircle(brush = border.brush, radius = radius + style.width / 2)
            } else {
                drawCircle(brush = border.brush, radius = radius, style = style)
            }
        }
    }
}

/**
 * Shape drawables use solid strokes regardless of [DivStroke.style], matching View, iOS and web.
 */
@Composable
internal fun DivStroke.observeSeparatorStroke(): BorderStroke {
    val color = color.observedColorValue()
    val width = width.observedDpValue(unit)
    return remember(color, width) { BorderStroke(width, color) }
}
