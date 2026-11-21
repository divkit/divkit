package com.yandex.div.compose.views.container

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.MultiMeasureLayout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import com.yandex.div.compose.collection.observeItems
import com.yandex.div.compose.collection.observeVisible
import com.yandex.div.compose.context.WithDivKey
import com.yandex.div.compose.di.WithLocalComponent
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.combineAlignment
import com.yandex.div.compose.utils.isMatchParent
import com.yandex.div.compose.utils.isWrapContent
import com.yandex.div.compose.utils.observeHorizontalMarginsSum
import com.yandex.div.compose.utils.observeVerticalMarginsSum
import com.yandex.div.compose.utils.observedAspectRatio
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.compose.utils.toHorizontalAlignment
import com.yandex.div.compose.utils.toVerticalAlignment
import com.yandex.div.compose.views.DivBlockLayoutParams
import com.yandex.div.compose.views.DivBlockView
import com.yandex.div.compose.views.modifiers.padding
import com.yandex.div2.DivBase
import com.yandex.div2.DivContainer
import com.yandex.div2.DivSize
import kotlin.math.max

/**
 * A container that lays its children out over each other. A wrap_content size next to match_parent
 * children is the size the other children define; when they define none, the largest content of
 * the match_parent children does. The match_parent children then fill that size.
 */
@Suppress("DEPRECATION")
@Composable
internal fun ContainerOverlapView(modifier: Modifier, data: DivContainer) {
    val horizontalAlignment = data.contentAlignmentHorizontal.observedValue()
    val verticalAlignment = data.contentAlignmentVertical.observedValue()
    val layoutParams = DivBlockLayoutParams(
        defaultHorizontalAlignment = horizontalAlignment.toCrossAxisHorizontalAlignment(),
        defaultVerticalAlignment = verticalAlignment.toCrossAxisVerticalAlignment(),
    )
    val items = data.observeItems()
    val wrapsWidth = data.width.isWrapContent
    // A positive aspect ratio makes the height follow the width: the children never wrap it.
    val wrapsHeight = data.height.isWrapContent && data.observedAspectRatio() == null

    // A match_parent child may be measured twice: as content, when nothing else defines the size
    // it fills, and at that size.
    MultiMeasureLayout(
        modifier = modifier
            .padding(data.paddings)
            .alignContainerOverflow(horizontalAlignment, verticalAlignment),
        content = {
            items.observeVisible().items.forEach { item ->
                WithDivKey(item) {
                    // The scope of the item, so its layout data is read where DivBlockView renders it.
                    WithLocalComponent(items.components?.get(item)) {
                        DivBlockView(
                            data = item,
                            modifier = item.value().observeOverlapChild(layoutParams, wrapsWidth, wrapsHeight),
                            layoutParams = layoutParams,
                        )
                    }
                }
            }
        },
        measurePolicy = OverlapMeasurePolicy,
    )
}

/**
 * How the overlap container lays a child out. A child fills an axis when it is match_parent along
 * a size the container wraps; [minWidth] and [minHeight] are the room such a child needs anyway,
 * its margins and min_size.
 */
private data class OverlapChild(
    val alignment: Alignment,
    val fillsWidth: Boolean,
    val fillsHeight: Boolean,
    val minWidth: Dp,
    val minHeight: Dp,
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any = this@OverlapChild
}

@Composable
private fun DivBase.observeOverlapChild(
    layoutParams: DivBlockLayoutParams,
    wrapsWidth: Boolean,
    wrapsHeight: Boolean,
): OverlapChild {
    val fillsWidth = wrapsWidth && width.isMatchParent
    val fillsHeight = wrapsHeight && height.isMatchParent
    return OverlapChild(
        alignment = combineAlignment(
            alignmentHorizontal?.observedValue()?.toHorizontalAlignment() ?: layoutParams.defaultHorizontalAlignment,
            alignmentVertical?.observedValue()?.toVerticalAlignment() ?: layoutParams.defaultVerticalAlignment,
        ),
        fillsWidth = fillsWidth,
        fillsHeight = fillsHeight,
        minWidth = if (fillsWidth) observeHorizontalMarginsSum() + width.observeMatchParentMinSize() else 0.dp,
        minHeight = if (fillsHeight) observeVerticalMarginsSum() + height.observeMatchParentMinSize() else 0.dp,
    )
}

/** The min_size of a match_parent size; bounds with min_size above max_size are ignored. */
@Composable
private fun DivSize.observeMatchParentMinSize(): Dp {
    val size = (this as? DivSize.MatchParent)?.value ?: return 0.dp
    val min = size.minSize?.observedValue() ?: return 0.dp
    val max = size.maxSize?.observedValue()
    return if (max != null && min > max) 0.dp else min
}

private val Measurable.overlapChild: OverlapChild?
    get() = parentData as? OverlapChild

private val Measurable.fillsWidth: Boolean
    get() = overlapChild?.fillsWidth == true

private val Measurable.fillsHeight: Boolean
    get() = overlapChild?.fillsHeight == true

private object OverlapMeasurePolicy : MeasurePolicy {

    override fun MeasureScope.measure(measurables: List<Measurable>, constraints: Constraints): MeasureResult {
        val loose = constraints.copy(minWidth = 0, minHeight = 0)
        val placeables = arrayOfNulls<Placeable>(measurables.size)
        // The children that fill nothing are measured once and define the sizes the others fill.
        measurables.forEachIndexed { index, measurable ->
            if (!measurable.fillsWidth && !measurable.fillsHeight) placeables[index] = measurable.measure(loose)
        }

        val filledWidth = filledWidth(measurables, placeables, constraints)
        val widthBound = if (filledWidth == null) loose else loose.copy(maxWidth = filledWidth)
        // A child that fills the width only has its height at that width.
        measurables.forEachIndexed { index, measurable ->
            if (measurable.fillsWidth && !measurable.fillsHeight) placeables[index] = measurable.measure(widthBound)
        }

        val filledHeight = filledHeight(measurables, placeables, constraints, filledWidth)
        measurables.forEachIndexed { index, measurable ->
            if (measurable.fillsHeight) {
                val bound = if (measurable.fillsWidth) widthBound else loose
                placeables[index] = measurable.measure(bound.copy(maxHeight = filledHeight ?: bound.maxHeight))
            }
        }

        // The passes above measure every child.
        val measured = placeables.requireNoNulls()
        val width = filledWidth ?: constraints.constrainWidth(measured.maxOfOrNull { it.width } ?: 0)
        val height = filledHeight ?: constraints.constrainHeight(measured.maxOfOrNull { it.height } ?: 0)
        return layout(width, height) {
            measured.forEach { placeable ->
                val alignment = (placeable.parentData as? OverlapChild)?.alignment ?: Alignment.TopStart
                val position = alignment.align(
                    size = IntSize(placeable.width, placeable.height),
                    space = IntSize(width, height),
                    layoutDirection = layoutDirection,
                )
                placeable.place(position)
            }
        }
    }

    /** The width the match_parent children fill, or null when no child fills the width. */
    private fun MeasureScope.filledWidth(
        measurables: List<Measurable>,
        placeables: Array<Placeable?>,
        constraints: Constraints,
    ): Int? {
        if (measurables.none { it.fillsWidth }) return null
        val loose = constraints.copy(minWidth = 0, minHeight = 0)
        var width = 0
        measurables.forEachIndexed { index, measurable ->
            if (!measurable.fillsWidth) {
                // A child that fills the height only is measured for its width without a height.
                val placeable = placeables[index] ?: measurable.measure(loose.copy(maxHeight = Constraints.Infinity))
                width = max(width, placeable.width)
            }
        }
        if (width == 0) {
            // Nothing else defines the width: the content of the match_parent children does. Without
            // any bound they do not fill, and a child with an aspect ratio does not derive its width
            // from the height.
            width = measurables.maxOf { if (it.fillsWidth) it.measure(Constraints()).width else 0 }
        }
        val minWidth = measurables.maxOf { it.overlapChild?.minWidth?.roundToPx() ?: 0 }
        // The size the container itself has to take, e.g. its own min_size, is filled as well.
        return constraints.constrainWidth(max(width, minWidth))
    }

    /** The height the match_parent children fill, or null when no child fills the height. */
    private fun MeasureScope.filledHeight(
        measurables: List<Measurable>,
        placeables: Array<Placeable?>,
        constraints: Constraints,
        filledWidth: Int?,
    ): Int? {
        if (measurables.none { it.fillsHeight }) return null
        var height = 0
        measurables.forEachIndexed { index, measurable ->
            if (!measurable.fillsHeight) height = max(height, placeables[index]?.height ?: 0)
        }
        if (height == 0) {
            height = measurables.maxOf { measurable ->
                val maxWidth = if (measurable.fillsWidth && filledWidth != null) filledWidth else constraints.maxWidth
                if (measurable.fillsHeight) measurable.measure(Constraints(maxWidth = maxWidth)).height else 0
            }
        }
        val minHeight = measurables.maxOf { it.overlapChild?.minHeight?.roundToPx() ?: 0 }
        return constraints.constrainHeight(max(height, minHeight))
    }
}
