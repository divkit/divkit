// Copyright 2026 Yandex LLC. All rights reserved.

package com.yandex.div.compose.views.modifiers

import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth

@Stable
internal fun Modifier.fixedSizeWithAlignment(
    width: Dp,
    height: Dp,
    alignment: Alignment,
): Modifier {
    if (!width.value.isFinite() || !height.value.isFinite()) {
        return wrapContentSize(alignment, unbounded = true).requiredSize(width, height)
    }
    return then(FixedSizeAlignmentElement(width, height, alignment))
}

private data class FixedSizeAlignmentElement(
    val width: Dp,
    val height: Dp,
    val alignment: Alignment,
) : ModifierNodeElement<FixedSizeAlignmentNode>() {

    override fun create() = FixedSizeAlignmentNode(width, height, alignment)

    override fun update(node: FixedSizeAlignmentNode) {
        node.width = width
        node.height = height
        node.alignment = alignment
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "fixedSizeWithAlignment"
        properties["width"] = width
        properties["height"] = height
        properties["alignment"] = alignment
    }
}

private class FixedSizeAlignmentNode(
    var width: Dp,
    var height: Dp,
    var alignment: Alignment,
) : Modifier.Node(), LayoutModifierNode {

    private val Density.targetConstraints: Constraints
        get() {
            val maxWidth = width.roundToPx().coerceAtLeast(0)
            val maxHeight = height.roundToPx().coerceAtLeast(0)
            // SizeNode treats a dimension rounded to Infinity as an unbounded axis.
            return Constraints(
                minWidth = if (maxWidth == Constraints.Infinity) 0 else maxWidth,
                maxWidth = maxWidth,
                minHeight = if (maxHeight == Constraints.Infinity) 0 else maxHeight,
                maxHeight = maxHeight,
            )
        }

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints,
    ): MeasureResult {
        val placeable = measurable.measure(targetConstraints)
        val width = constraints.constrainWidth(placeable.width)
        val height = constraints.constrainHeight(placeable.height)
        return layout(width, height) {
            val position = alignment.align(
                IntSize.Zero,
                IntSize(width - placeable.width, height - placeable.height),
                layoutDirection,
            )
            placeable.place(position)
        }
    }

    override fun IntrinsicMeasureScope.minIntrinsicWidth(
        measurable: IntrinsicMeasurable,
        height: Int,
    ): Int {
        Constraints(maxHeight = height)
        val target = targetConstraints
        return if (target.hasFixedWidth) target.maxWidth else {
            with(FixedSizeIntrinsicFallback(target)) { minIntrinsicWidth(measurable, height) }
        }
    }

    override fun IntrinsicMeasureScope.maxIntrinsicWidth(
        measurable: IntrinsicMeasurable,
        height: Int,
    ): Int {
        Constraints(maxHeight = height)
        val target = targetConstraints
        return if (target.hasFixedWidth) target.maxWidth else {
            with(FixedSizeIntrinsicFallback(target)) { maxIntrinsicWidth(measurable, height) }
        }
    }

    override fun IntrinsicMeasureScope.minIntrinsicHeight(
        measurable: IntrinsicMeasurable,
        width: Int,
    ): Int {
        Constraints(maxWidth = width)
        val target = targetConstraints
        return if (target.hasFixedHeight) target.maxHeight else {
            with(FixedSizeIntrinsicFallback(target)) { minIntrinsicHeight(measurable, width) }
        }
    }

    override fun IntrinsicMeasureScope.maxIntrinsicHeight(
        measurable: IntrinsicMeasurable,
        width: Int,
    ): Int {
        Constraints(maxWidth = width)
        val target = targetConstraints
        return if (target.hasFixedHeight) target.maxHeight else {
            with(FixedSizeIntrinsicFallback(target)) { maxIntrinsicHeight(measurable, width) }
        }
    }
}

// Only used for axes rounded to Infinity, preserving framework intrinsic size validation.
private class FixedSizeIntrinsicFallback(val target: Constraints) : LayoutModifier {
    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints,
    ): MeasureResult {
        val placeable = measurable.measure(target)
        return layout(
            constraints.constrainWidth(placeable.width),
            constraints.constrainHeight(placeable.height),
        ) {}
    }
}
