package com.yandex.div.compose.views.container

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.observeHorizontalInsets
import com.yandex.div.compose.utils.toVerticalAlignment
import com.yandex.div2.Div
import com.yandex.div2.DivAlignmentVertical
import com.yandex.div2.DivSize
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
internal fun MatchParentRow(
    modifier: Modifier,
    horizontalArrangement: Arrangement.Horizontal,
    verticalAlignment: Alignment.Vertical,
    content: @Composable () -> Unit,
) {
    Layout(
        content = content,
        modifier = modifier,
        measurePolicy = remember(horizontalArrangement, verticalAlignment) {
            MatchParentRowMeasurePolicy(horizontalArrangement, verticalAlignment)
        },
    )
}

@Composable
internal fun observeMatchParentRowChild(div: Div, alignByBaseline: Boolean): MatchParentRowChild {
    val child = div.value()
    val alignment = child.alignmentVertical?.observedValue()
    val spec = (child.width as? DivSize.MatchParent)?.value?.let { size ->
        val (startMargin, endMargin) = child.margins.observeHorizontalInsets()
        size.observeMatchParentSpec(startMargin, endMargin)
    }
    return MatchParentRowChild(
        alignment = alignment?.takeUnless { it == DivAlignmentVertical.BASELINE }?.toVerticalAlignment(),
        alignByBaseline = alignment == DivAlignmentVertical.BASELINE || alignment == null && alignByBaseline,
        spec = spec,
        isConstrained = (child.width as? DivSize.WrapContent)?.value?.constrained?.observedValue() == true,
    )
}

internal data class MatchParentRowChild(
    val alignment: Alignment.Vertical? = null,
    val alignByBaseline: Boolean = false,
    val spec: MatchParentSpec? = null,
    val isConstrained: Boolean = false,
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any = this@MatchParentRowChild
}

internal val IntrinsicMeasurable.matchParentRowSpec: MatchParentSpec?
    get() = (parentData as? MatchParentRowChild)?.spec

internal val IntrinsicMeasurable.isConstrainedInRow: Boolean
    get() = (parentData as? MatchParentRowChild)?.isConstrained == true

private class MatchParentRowMeasurePolicy(
    private val arrangement: Arrangement.Horizontal,
    private val verticalAlignment: Alignment.Vertical,
) : MeasurePolicy {

    override fun MeasureScope.measure(measurables: List<Measurable>, constraints: Constraints): MeasureResult {
        val placeables = arrayOfNulls<Placeable>(measurables.size)
        val specs = measurables.mapNotNull { it.matchParentRowSpec }
        val fixedSpace = measureUnweighted(measurables, placeables, specs, constraints)
        val target = if (constraints.hasBoundedWidth) constraints.maxWidth else constraints.minWidth
        val sizes = allocateMatchParentSizes(specs, target - fixedSpace, this)
        var matchParentIndex = 0
        measurables.forEachIndexed { index, measurable ->
            val spec = measurable.matchParentRowSpec ?: return@forEachIndexed
            val width = sizes[matchParentIndex++] + spec.marginsPx
            placeables[index] = measurable.measure(
                Constraints(minWidth = width, maxWidth = width, maxHeight = constraints.maxHeight)
            )
        }

        val measured = placeables.requireNoNulls()
        val firstBaseline = measured.maxOfOrNull { it.alignedBaseline ?: 0 } ?: 0
        val afterBaseline = measured.maxOfOrNull { placeable ->
            placeable.alignedBaseline?.let { placeable.height - it } ?: 0
        } ?: 0
        val height = constraints.constrainHeight(
            max(measured.maxOfOrNull { it.height } ?: 0, firstBaseline + afterBaseline)
        )
        val width = constraints.constrainWidth(fixedSpace + sizes.sum())
        return placeChildren(measured, width, height, firstBaseline)
    }

    private fun MeasureScope.measureUnweighted(
        measurables: List<Measurable>,
        placeables: Array<Placeable?>,
        specs: List<MatchParentSpec>,
        constraints: Constraints,
    ): Int {
        val minSizes = specs.sumOf { it.minSize.roundToPx() }
        var fixedSpace = spacingTotal(measurables.size) + specs.sumOf { it.marginsPx }
        measurables.forEachUnweighted(constrainedLast = minSizes > 0) { index, measurable ->
            val remaining = if (constraints.hasBoundedWidth) {
                val reserved = if (measurable.isConstrainedInRow) minSizes else 0
                (constraints.maxWidth - fixedSpace - reserved).coerceAtLeast(0)
            } else {
                Constraints.Infinity
            }
            val placeable = measurable.measure(Constraints(maxWidth = remaining, maxHeight = constraints.maxHeight))
            placeables[index] = placeable
            fixedSpace += placeable.width
        }
        return fixedSpace
    }

    private fun MeasureScope.placeChildren(
        measured: Array<Placeable>,
        width: Int,
        height: Int,
        firstBaseline: Int,
    ): MeasureResult {
        val positions = IntArray(measured.size)
        val childWidths = IntArray(measured.size) { measured[it].width }
        with(arrangement) { arrange(width, childWidths, layoutDirection, positions) }
        return layout(width, height) {
            measured.forEachIndexed { index, placeable ->
                val data = placeable.parentData as? MatchParentRowChild
                val y = if (data?.alignByBaseline == true) {
                    placeable.alignedBaseline?.let { firstBaseline - it } ?: 0
                } else {
                    (data?.alignment ?: verticalAlignment).align(placeable.height, height)
                }
                placeable.place(positions[index], y)
            }
        }
    }

    override fun IntrinsicMeasureScope.minIntrinsicWidth(measurables: List<IntrinsicMeasurable>, height: Int): Int =
        intrinsicWidth(measurables) { minIntrinsicWidth(height) }

    override fun IntrinsicMeasureScope.maxIntrinsicWidth(measurables: List<IntrinsicMeasurable>, height: Int): Int =
        intrinsicWidth(measurables) { maxIntrinsicWidth(height) }

    override fun IntrinsicMeasureScope.minIntrinsicHeight(measurables: List<IntrinsicMeasurable>, width: Int): Int =
        intrinsicHeight(measurables, width) { minIntrinsicHeight(it) }

    override fun IntrinsicMeasureScope.maxIntrinsicHeight(measurables: List<IntrinsicMeasurable>, width: Int): Int =
        intrinsicHeight(measurables, width) { maxIntrinsicHeight(it) }

    private fun Density.intrinsicWidth(
        measurables: List<IntrinsicMeasurable>,
        intrinsicWidth: IntrinsicMeasurable.() -> Int,
    ): Int {
        var fixedSpace = spacingTotal(measurables.size)
        var unit = 0
        var totalWeight = 0f
        measurables.forEach { measurable ->
            val spec = measurable.matchParentRowSpec
            if (spec == null) {
                fixedSpace += measurable.intrinsicWidth()
            } else {
                fixedSpace += spec.marginsPx
                totalWeight += spec.weight
                val contentWidth = (measurable.intrinsicWidth() - spec.marginsPx).coerceAtLeast(0)
                unit = max(unit, (contentWidth / spec.weight).roundToInt())
            }
        }
        return fixedSpace + (unit * totalWeight).roundToInt()
    }

    private fun Density.intrinsicHeight(
        measurables: List<IntrinsicMeasurable>,
        width: Int,
        intrinsicHeight: IntrinsicMeasurable.(width: Int) -> Int,
    ): Int {
        val specs = measurables.mapNotNull { it.matchParentRowSpec }
        val minSizes = specs.sumOf { it.minSize.roundToPx() }
        var fixedSpace = spacingTotal(measurables.size) + specs.sumOf { it.marginsPx }
        var height = 0
        measurables.forEachUnweighted(constrainedLast = minSizes > 0) { _, measurable ->
            val remaining = if (width != Constraints.Infinity) {
                val reserved = if (measurable.isConstrainedInRow) minSizes else 0
                (width - fixedSpace - reserved).coerceAtLeast(0)
            } else {
                Constraints.Infinity
            }
            val childWidth = measurable.maxIntrinsicWidth(Constraints.Infinity).coerceAtMost(remaining)
            fixedSpace += childWidth
            height = max(height, measurable.intrinsicHeight(childWidth))
        }
        val sizes = if (width == Constraints.Infinity) null else {
            allocateMatchParentSizes(specs, width - fixedSpace, this)
        }
        var matchParentIndex = 0
        measurables.forEach { measurable ->
            val spec = measurable.matchParentRowSpec ?: return@forEach
            val childWidth = sizes?.let { it[matchParentIndex++] + spec.marginsPx } ?: Constraints.Infinity
            height = max(height, measurable.intrinsicHeight(childWidth))
        }
        return height
    }

    private val Placeable.alignedBaseline: Int?
        get() = if ((parentData as? MatchParentRowChild)?.alignByBaseline == true) {
            get(FirstBaseline).takeIf { it != AlignmentLine.Unspecified }
        } else {
            null
        }

    private fun Density.spacingTotal(childCount: Int): Int =
        arrangement.spacing.roundToPx() * (childCount - 1).coerceAtLeast(0)
}

private inline fun <T : IntrinsicMeasurable> List<T>.forEachUnweighted(
    constrainedLast: Boolean,
    action: (Int, T) -> Unit,
) {
    forEachIndexed { index, measurable ->
        if (measurable.matchParentRowSpec == null && (!constrainedLast || !measurable.isConstrainedInRow)) {
            action(index, measurable)
        }
    }
    if (constrainedLast) {
        forEachIndexed { index, measurable ->
            if (measurable.matchParentRowSpec == null && measurable.isConstrainedInRow) action(index, measurable)
        }
    }
}
