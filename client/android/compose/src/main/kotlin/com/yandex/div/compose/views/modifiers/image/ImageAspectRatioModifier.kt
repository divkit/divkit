package com.yandex.div.compose.views.modifiers.image

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.observedValue
import com.yandex.div2.DivSize
import kotlin.math.roundToInt

internal const val MAX_LAYOUT_SIZE = Constraints.Infinity - 1

@Composable
internal fun Modifier.imageAspectRatio(
    ratio: Float,
    height: DivSize,
    alignment: Alignment,
    fillMatchParentHeight: Boolean,
    matchHeightConstraintsFirst: Boolean,
): Modifier {
    val bounds = if (matchHeightConstraintsFirst && height is DivSize.WrapContent) {
        HeightBounds()
    } else {
        height.observedHeightBounds(fillMatchParentHeight)
    }
    return this then ImageAspectRatioElement(
        ratio = ratio,
        height = bounds,
        alignment = alignment,
        matchHeightConstraintsFirst = matchHeightConstraintsFirst,
        reporter = LocalComponent.current.reporter,
    )
}

private data class HeightBounds(
    val min: Dp? = null,
    val max: Dp? = null,
    val isConstrained: Boolean = false,
    val isFixed: Boolean = false,
)

@Composable
private fun DivSize.observedHeightBounds(fillMatchParentHeight: Boolean): HeightBounds {
    val bounds = when (this) {
        is DivSize.Fixed -> value.observedValue().let { HeightBounds(min = it, max = it, isFixed = true) }
        is DivSize.WrapContent -> HeightBounds(
            min = value.minSize?.observedValue(),
            max = value.maxSize?.observedValue(),
            isConstrained = value.constrained?.observedValue() == true,
        )
        is DivSize.MatchParent -> HeightBounds(
            min = value.minSize?.observedValue(),
            max = value.maxSize?.observedValue(),
            isConstrained = fillMatchParentHeight,
        )
    }
    return if (bounds.min != null && bounds.max != null && bounds.min > bounds.max) {
        bounds.copy(min = null, max = null)
    } else {
        bounds
    }
}

private data class ImageAspectRatioElement(
    val ratio: Float,
    val height: HeightBounds,
    val alignment: Alignment,
    val matchHeightConstraintsFirst: Boolean,
    val reporter: DivReporter,
) : ModifierNodeElement<ImageAspectRatioNode>() {

    override fun create() = ImageAspectRatioNode(this)

    override fun update(node: ImageAspectRatioNode) {
        node.parameters = this
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "imageAspectRatio"
        properties["ratio"] = ratio
    }
}

private class ImageAspectRatioNode(
    var parameters: ImageAspectRatioElement,
) : Modifier.Node(), LayoutModifierNode {

    private var lastClampedSize: IntSize? = null

    override fun MeasureScope.measure(measurable: Measurable, constraints: Constraints): MeasureResult {
        val bounds = parameters.height
        val requestedHeight = (if (bounds.isConstrained) constraints.maxHeight else Constraints.Infinity)
            .coerceIn(bounds.min?.roundToPx() ?: 0, bounds.max?.roundToPx() ?: Constraints.Infinity)
        val heightFirst = parameters.matchHeightConstraintsFirst &&
            (bounds.isFixed || bounds.isConstrained && constraints.hasBoundedHeight)
        val width = if (heightFirst) {
            if (requestedHeight == 0) 0 else (requestedHeight * parameters.ratio).roundToInt()
        } else {
            constraints.constrainWidth(measurable.maxIntrinsicWidth(requestedHeight))
        }.coerceAtMost(MAX_LAYOUT_SIZE)
        val height = if (heightFirst) requestedHeight else (width / parameters.ratio).roundToInt()
        val childConstraints = fixedWithinLayoutLimits(width, height.coerceAtMost(MAX_LAYOUT_SIZE), heightFirst)
        val placeable = measurable.measure(childConstraints)
        val size = IntSize(
            constraints.constrainWidth(childConstraints.maxWidth),
            constraints.constrainHeight(childConstraints.maxHeight),
        )
        return layout(size.width, size.height) {
            val offset = parameters.alignment.align(
                IntSize(placeable.width, placeable.height),
                size,
                layoutDirection,
            )
            placeable.place(offset)
        }
    }

    private fun fixedWithinLayoutLimits(width: Int, height: Int, heightFirst: Boolean): Constraints {
        val constraints = if (heightFirst) {
            Constraints.fitPrioritizingHeight(width, width, height, height)
        } else {
            Constraints.fitPrioritizingWidth(width, width, height, height)
        }
        val clampedSize = IntSize(width, height)
            .takeIf { constraints.maxWidth != width || constraints.maxHeight != height }
        if (clampedSize == lastClampedSize) return constraints

        lastClampedSize = clampedSize
        if (clampedSize != null) {
            val type = if (parameters.matchHeightConstraintsFirst) "div-gif-image" else "div-image"
            parameters.reporter.reportWarning(
                "$type size exceeds Compose layout limits: ${width}x$height, " +
                    "clamped to ${constraints.maxWidth}x${constraints.maxHeight}"
            )
        }
        return constraints
    }
}
