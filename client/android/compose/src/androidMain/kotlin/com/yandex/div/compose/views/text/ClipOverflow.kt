package com.yandex.div.compose.views.text

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Constraints

/**
 * Whether the content is taller than the height left to it. A layout modifier placed after
 * [clipOverflow] writes the flag while it is measured, and [clipOverflow] reads it right after:
 * both modifiers belong to the same layout node and are always measured together, so no snapshot
 * state is needed.
 */
internal class ClipOverflow {
    var clips = false
}

/**
 * Clips the overflowing content to its bounds, both the drawing and the hit testing. The layer
 * exists only while [overflow] reports clipping: the content that fits keeps its shadows and does
 * not pay for a layer.
 */
internal fun Modifier.clipOverflow(overflow: ClipOverflow): Modifier {
    return then(ClipOverflowElement(overflow))
}

private data class ClipOverflowElement(
    val overflow: ClipOverflow,
) : ModifierNodeElement<ClipOverflowNode>() {
    override fun create() = ClipOverflowNode(overflow)

    override fun update(node: ClipOverflowNode) {
        node.overflow = overflow
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "clipOverflow"
    }
}

private class ClipOverflowNode(
    var overflow: ClipOverflow,
) : Modifier.Node(), LayoutModifierNode {

    override fun MeasureScope.measure(measurable: Measurable, constraints: Constraints): MeasureResult {
        val placeable = measurable.measure(constraints)
        val clips = overflow.clips
        return layout(placeable.width, placeable.height) {
            if (clips) {
                placeable.placeWithLayer(0, 0, layerBlock = ClipToBounds)
            } else {
                placeable.place(0, 0)
            }
        }
    }

    override fun IntrinsicMeasureScope.minIntrinsicWidth(measurable: IntrinsicMeasurable, height: Int) =
        measurable.minIntrinsicWidth(height)

    override fun IntrinsicMeasureScope.maxIntrinsicWidth(measurable: IntrinsicMeasurable, height: Int) =
        measurable.maxIntrinsicWidth(height)

    override fun IntrinsicMeasureScope.minIntrinsicHeight(measurable: IntrinsicMeasurable, width: Int) =
        measurable.minIntrinsicHeight(width)

    override fun IntrinsicMeasureScope.maxIntrinsicHeight(measurable: IntrinsicMeasurable, width: Int) =
        measurable.maxIntrinsicHeight(width)
}

private val ClipToBounds: GraphicsLayerScope.() -> Unit = { clip = true }
