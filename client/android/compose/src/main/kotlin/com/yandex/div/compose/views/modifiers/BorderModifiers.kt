package com.yandex.div.compose.views.modifiers

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.yandex.div.compose.expressions.observedColorValue
import com.yandex.div.compose.expressions.observedFloatValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.observedDpValue
import com.yandex.div.compose.utils.observedRoundedCornerShape
import com.yandex.div.compose.utils.toDp
import com.yandex.div2.DivBorder
import com.yandex.div2.DivShadow
import com.yandex.div2.DivStrokeStyle
import kotlin.math.max
import kotlin.math.min

@Composable
internal fun Modifier.borderClip(data: DivBorder): Modifier {
    val shape = data.toShape()
    if (shape == RectangleShape) return this
    // Under a solid opaque stroke the clip (background and content) is inset so that its
    // anti-aliased edge is hidden under the fully opaque part of the stroke. If the clip
    // ended at the outline instead, it would attenuate the background and the stroke at the
    // same boundary pixels and the background would show through the stroke's outer edge
    // as a thin halo. A dashed stroke has gaps where the background must reach the outline,
    // so the inset is not applied.
    val stroke = data.stroke ?: return clip(shape)
    val color = stroke.color.observedColorValue()
    val width = stroke.width.observedDpValue(stroke.unit)
    val isSolid = stroke.style !is DivStrokeStyle.Dashed
    val clipShape = if (isSolid && color.alpha == 1f && width > 0.dp) {
        StrokeInsetShape(shape, width)
    } else {
        shape
    }
    return clip(clipShape)
}

private data class StrokeInsetShape(
    private val shape: Shape,
    private val strokeWidth: Dp,
) : Shape {

    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val outline = shape.createOutline(size, layoutDirection, density)
        val widthPx = with(density) { strokeWidth.toPx() }
        val strokeOffset = with(density) {
            min(0.5.dp.toPx(), max(1f, widthPx * STROKE_OFFSET_PERCENTAGE))
        }
        // The outer max matters on sub-1x densities where 0.5dp is less than a full pixel:
        // the inset must cover at least one pixel of the clip's edge. Never inset deeper
        // than the stroke itself, or the background would peek out at its inner edge.
        val inset = min(max(1f, strokeOffset), widthPx).coerceAtMost(size.minDimension / 2f)
        return when (outline) {
            is Outline.Rectangle -> Outline.Rectangle(outline.rect.deflate(inset))
            is Outline.Rounded -> with(outline.roundRect) {
                Outline.Rounded(
                    RoundRect(
                        left = left + inset,
                        top = top + inset,
                        right = right - inset,
                        bottom = bottom - inset,
                        topLeftCornerRadius = topLeftCornerRadius.shrink(inset),
                        topRightCornerRadius = topRightCornerRadius.shrink(inset),
                        bottomRightCornerRadius = bottomRightCornerRadius.shrink(inset),
                        bottomLeftCornerRadius = bottomLeftCornerRadius.shrink(inset),
                    )
                )
            }
            is Outline.Generic -> outline
        }
    }
}

// The stroke is drawn over the content, outside the border clip layer, with its outer edge
// exactly on the clip outline. A stroke drawn inside the clip would be attenuated by the
// anti-aliased clip; the background edge under the stroke is hidden by the clip inset
// instead (see StrokeInsetShape).
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

// Unlike a plain drawOutline stroke (centered on the outline), the dashed stroke path is
// inset by half the width so that it stays within the outline: drawn outside the clip
// layer, a centered stroke would leak past the rounded bounds with nothing to trim it.
@Composable
private fun Modifier.dashedBorder(
    color: Color,
    width: Dp,
    shape: Shape
): Modifier {
    return drawWithCache {
        val widthPx = width.toPx()
        if (widthPx <= 0f) {
            return@drawWithCache onDrawWithContent { drawContent() }
        }
        val style = Stroke(
            width = widthPx,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(DASH_WIDTH.toPx(), GAP_WIDTH.toPx()), 0f),
        )
        val inset = min(widthPx / 2f, size.minDimension / 2f)
        val path = Path()
        // A corner radius below half the stroke width clamps the dash path radius to zero,
        // and the miter-joined stroke corner pokes past the rounded outline. Drawn outside
        // the clip layer nothing trims it, so in that case the stroke is clipped to the
        // outline explicitly. Null in the common case, so drawing stays clip-free.
        var trimPath: Path? = null
        when (val outline = shape.createOutline(size, layoutDirection, this)) {
            is Outline.Rectangle -> with(outline.rect) {
                path.addRect(Rect(left + inset, top + inset, right - inset, bottom - inset))
            }
            is Outline.Rounded -> with(outline.roundRect) {
                path.addRoundRect(
                    RoundRect(
                        left = left + inset,
                        top = top + inset,
                        right = right - inset,
                        bottom = bottom - inset,
                        topLeftCornerRadius = topLeftCornerRadius.shrink(inset),
                        topRightCornerRadius = topRightCornerRadius.shrink(inset),
                        bottomRightCornerRadius = bottomRightCornerRadius.shrink(inset),
                        bottomLeftCornerRadius = bottomLeftCornerRadius.shrink(inset),
                    )
                )
                if (hasCornerRadiusBelow(widthPx / 2f)) {
                    trimPath = Path().apply { addRoundRect(outline.roundRect) }
                }
            }
            is Outline.Generic -> path.addPath(outline.path)
        }
        onDrawWithContent {
            drawContent()
            val trim = trimPath
            if (trim != null) {
                clipPath(trim) {
                    drawPath(path, color, style = style)
                }
            } else {
                drawPath(path, color, style = style)
            }
        }
    }
}

private fun RoundRect.hasCornerRadiusBelow(threshold: Float): Boolean {
    return listOf(topLeftCornerRadius, topRightCornerRadius, bottomRightCornerRadius, bottomLeftCornerRadius)
        .any { (it.x > 0f && it.x < threshold) || (it.y > 0f && it.y < threshold) }
}

private fun CornerRadius.shrink(delta: Float): CornerRadius =
    CornerRadius(max(0f, x - delta), max(0f, y - delta))

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
            color = color.copy(alpha = color.alpha * alpha),
            offset = DpOffset(offsetX, offsetY),
        )
    )
}

private val DEFAULT_SHADOW_ELEVATION = 3.dp
private val DEFAULT_SHADOW_OFFSET_X = 0.dp
private val DEFAULT_SHADOW_OFFSET_Y = 0.5.dp
private const val DEFAULT_SHADOW_ALPHA = 0.14f
private val DEFAULT_SHADOW_COLOR = Color.Black
// Keep in sync with STROKE_OFFSET_PERCENTAGE in the View client (DivBorderDrawer.kt).
private const val STROKE_OFFSET_PERCENTAGE = 0.1f
private val DASH_WIDTH = 6.dp
private val GAP_WIDTH = 2.dp
