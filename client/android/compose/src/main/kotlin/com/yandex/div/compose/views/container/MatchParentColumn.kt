package com.yandex.div.compose.views.container

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import com.yandex.div.compose.expressions.observedFloatValue
import com.yandex.div.compose.utils.observedDpValue
import com.yandex.div2.DivMatchParentSize
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * A Column for a container with match_parent children along its definite height. The other
 * children are measured first, in order and within the space left, as in a Column; the
 * match_parent children then share what remains the way the View renderer does: by weight, with a
 * child clamped by its min_size or max_size leaving the pool and the rest shared again. A Column
 * sizes a weighted child once by its share, so a max_size clamp would leave a gap. The margins of
 * a match_parent child are reserved before anything is shared, so its share sizes the content, as
 * in the View renderer. Every child is measured once, at its final size.
 */
@Composable
internal fun MatchParentColumn(
    modifier: Modifier,
    verticalArrangement: Arrangement.Vertical,
    horizontalAlignment: Alignment.Horizontal,
    content: @Composable () -> Unit,
) {
    Layout(
        content = content,
        modifier = modifier,
        measurePolicy = remember(verticalArrangement, horizontalAlignment) {
            MatchParentColumnMeasurePolicy(verticalArrangement, horizontalAlignment)
        },
    )
}

/** Aligns a child of [MatchParentColumn] horizontally. */
internal fun Modifier.alignInMatchParentColumn(alignment: Alignment.Horizontal): Modifier =
    then(MatchParentColumnAlignModifier(alignment))

/**
 * Marks a child of [MatchParentColumn] as a match_parent child with the bounds and weight of [size];
 * [margins] is the child's vertical margins, kept outside its share and inside its measured size.
 */
@Composable
internal fun Modifier.matchParentHeight(size: DivMatchParentSize, margins: Dp): Modifier {
    val minSize = size.minSize?.let { it.value.observedDpValue(it.unit) } ?: 0.dp
    val maxSize = size.maxSize?.let { it.value.observedDpValue(it.unit) }
    val hasValidBounds = maxSize == null || minSize <= maxSize
    val spec = MatchParentSpec(
        weight = size.weight?.observedFloatValue()?.takeIf { it > 0f } ?: 1f,
        minSize = if (hasValidBounds) minSize else 0.dp,
        maxSize = if (hasValidBounds) maxSize else null,
        margins = margins,
    )
    return then(MatchParentColumnSpecModifier(spec))
}

internal data class MatchParentSpec(val weight: Float, val minSize: Dp, val maxSize: Dp?, val margins: Dp)

private data class MatchParentColumnChild(
    val alignment: Alignment.Horizontal? = null,
    val spec: MatchParentSpec? = null,
)

private data class MatchParentColumnAlignModifier(private val alignment: Alignment.Horizontal) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any =
        (parentData as? MatchParentColumnChild ?: MatchParentColumnChild()).copy(alignment = alignment)
}

private data class MatchParentColumnSpecModifier(private val spec: MatchParentSpec) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any =
        (parentData as? MatchParentColumnChild ?: MatchParentColumnChild()).copy(spec = spec)
}

private val IntrinsicMeasurable.matchParentSpec: MatchParentSpec?
    get() = (parentData as? MatchParentColumnChild)?.spec

private class MatchParentColumnMeasurePolicy(
    private val arrangement: Arrangement.Vertical,
    private val horizontalAlignment: Alignment.Horizontal,
) : MeasurePolicy {

    override fun MeasureScope.measure(measurables: List<Measurable>, constraints: Constraints): MeasureResult {
        val childConstraints = Constraints(maxWidth = constraints.maxWidth)
        val placeables = arrayOfNulls<Placeable>(measurables.size)
        val specs = measurables.mapNotNull { it.matchParentSpec }
        var fixedSpace = spacingTotal(measurables.size) + specs.sumOf { it.margins.roundToPx() }

        measurables.forEachIndexed { index, measurable ->
            if (measurable.matchParentSpec != null) return@forEachIndexed
            val remaining = if (constraints.hasBoundedHeight) {
                (constraints.maxHeight - fixedSpace).coerceAtLeast(0)
            } else {
                Constraints.Infinity
            }
            val placeable = measurable.measure(childConstraints.copy(maxHeight = remaining))
            placeables[index] = placeable
            fixedSpace += placeable.height
        }

        // Without a bound there is nothing to share: the children keep their min_size, as in a Column.
        val target = if (constraints.hasBoundedHeight) constraints.maxHeight else constraints.minHeight
        val sizes = allocateMatchParentSizes(specs, target - fixedSpace, this)
        var matchParentIndex = 0
        measurables.forEachIndexed { index, measurable ->
            val spec = measurable.matchParentSpec ?: return@forEachIndexed
            val size = sizes[matchParentIndex++] + spec.margins.roundToPx()
            placeables[index] = measurable.measure(childConstraints.copy(minHeight = size, maxHeight = size))
        }

        // Overflow starts at the top and is cut at the bottom, as in the View renderer; the raw size a
        // Column reports would make Compose center it.
        val height = constraints.constrainHeight(fixedSpace + sizes.sum())
        // The two passes above measure every child.
        val measured = placeables.requireNoNulls()
        // A child may emit no node, e.g. a custom element without a factory.
        val width = constraints.constrainWidth(measured.maxOfOrNull { it.width } ?: 0)
        val childHeights = IntArray(measured.size) { measured[it].height }
        val positions = IntArray(measured.size)
        with(arrangement) { arrange(height, childHeights, positions) }
        return layout(width, height) {
            measured.forEachIndexed { index, placeable ->
                val alignment = (placeable.parentData as? MatchParentColumnChild)?.alignment ?: horizontalAlignment
                placeable.place(alignment.align(placeable.width, width, layoutDirection), positions[index])
            }
        }
    }

    // The intrinsic sizes follow the Column formulas: the other children and the margins of the
    // match_parent children fix the space, the match_parent children share the rest by weight and
    // are asked only for the width of that share, as a Column asks its weighted children.
    override fun IntrinsicMeasureScope.minIntrinsicWidth(measurables: List<IntrinsicMeasurable>, height: Int): Int =
        intrinsicWidth(measurables, height, { minIntrinsicHeight(it) }, { minIntrinsicWidth(it) })

    override fun IntrinsicMeasureScope.maxIntrinsicWidth(measurables: List<IntrinsicMeasurable>, height: Int): Int =
        intrinsicWidth(measurables, height, { maxIntrinsicHeight(it) }, { maxIntrinsicWidth(it) })

    override fun IntrinsicMeasureScope.minIntrinsicHeight(measurables: List<IntrinsicMeasurable>, width: Int): Int =
        intrinsicHeight(measurables) { minIntrinsicHeight(width) }

    override fun IntrinsicMeasureScope.maxIntrinsicHeight(measurables: List<IntrinsicMeasurable>, width: Int): Int =
        intrinsicHeight(measurables) { maxIntrinsicHeight(width) }

    private fun Density.intrinsicWidth(
        measurables: List<IntrinsicMeasurable>,
        height: Int,
        intrinsicHeight: IntrinsicMeasurable.(width: Int) -> Int,
        intrinsicWidth: IntrinsicMeasurable.(height: Int) -> Int,
    ): Int {
        var fixedSpace = spacingTotal(measurables.size)
        var width = 0
        var totalWeight = 0f
        measurables.forEach { measurable ->
            val spec = measurable.matchParentSpec
            if (spec == null) {
                val childHeight = measurable.intrinsicHeight(Constraints.Infinity)
                fixedSpace += childHeight
                width = max(width, measurable.intrinsicWidth(childHeight))
            } else {
                totalWeight += spec.weight
                fixedSpace += spec.margins.roundToPx()
            }
        }
        if (totalWeight == 0f) return width
        val unit = (height - fixedSpace).coerceAtLeast(0) / totalWeight
        measurables.forEach { measurable ->
            val spec = measurable.matchParentSpec ?: return@forEach
            val share = (unit * spec.weight).roundToInt() + spec.margins.roundToPx()
            width = max(width, measurable.intrinsicWidth(share))
        }
        return width
    }

    private fun Density.intrinsicHeight(
        measurables: List<IntrinsicMeasurable>,
        intrinsicHeight: IntrinsicMeasurable.() -> Int,
    ): Int {
        var fixedSpace = spacingTotal(measurables.size)
        var unit = 0
        var totalWeight = 0f
        measurables.forEach { measurable ->
            val spec = measurable.matchParentSpec
            if (spec == null) {
                fixedSpace += measurable.intrinsicHeight()
            } else {
                val margins = spec.margins.roundToPx()
                totalWeight += spec.weight
                fixedSpace += margins
                unit = max(unit, ((measurable.intrinsicHeight() - margins) / spec.weight).roundToInt())
            }
        }
        return fixedSpace + (unit * totalWeight).roundToInt()
    }

    private fun Density.spacingTotal(childCount: Int): Int =
        arrangement.spacing.roundToPx() * (childCount - 1).coerceAtLeast(0)
}

/**
 * Shares [space] among [specs] by weight, resolving min_size and max_size the way flexbox resolves
 * flexible lengths: each round every child not yet frozen takes its share clamped to its bounds;
 * if the clamps add space, the children held at min_size are frozen, if they take space, those held
 * at max_size are, and the rest share again. Without space to share every child keeps its min_size.
 */
private fun allocateMatchParentSizes(specs: List<MatchParentSpec>, space: Int, density: Density): IntArray {
    val minSizes = IntArray(specs.size) { with(density) { specs[it].minSize.roundToPx() } }
    if (space <= 0) return minSizes
    val maxSizes = IntArray(specs.size) { with(density) { specs[it].maxSize?.roundToPx() ?: Int.MAX_VALUE } }
    val sizes = resolveFlexibleSizes(FloatArray(specs.size) { specs[it].weight }, minSizes, maxSizes, space)
    return roundWithinBounds(sizes, minSizes, maxSizes)
}

private fun resolveFlexibleSizes(weights: FloatArray, minSizes: IntArray, maxSizes: IntArray, space: Int): FloatArray {
    val flexible = FlexibleSizes(weights, minSizes, maxSizes, space)
    while (flexible.shareRound()) Unit
    return flexible.sizes
}

/** The children not frozen yet share what the frozen ones leave, clamped to their bounds. */
private class FlexibleSizes(
    private val weights: FloatArray,
    private val minSizes: IntArray,
    private val maxSizes: IntArray,
    private val space: Int,
) {
    val sizes = FloatArray(weights.size)
    private val frozen = BooleanArray(weights.size)

    /** Shares the space once and freezes the violators; returns whether another round is needed. */
    fun shareRound(): Boolean {
        var freeSpace = space.toFloat()
        var weightSum = 0f
        weights.indices.forEach { if (frozen[it]) freeSpace -= sizes[it] else weightSum += weights[it] }
        if (weightSum <= 0f) return false
        var violation = 0f
        weights.indices.filterNot { frozen[it] }.forEach {
            val share = freeSpace * weights[it] / weightSum
            sizes[it] = share.coerceIn(minSizes[it].toFloat(), maxSizes[it].toFloat())
            violation += sizes[it] - share
        }
        if (violation == 0f) return false
        val bounds = if (violation > 0f) minSizes else maxSizes
        val violators = weights.indices.filter { !frozen[it] && sizes[it] == bounds[it].toFloat() }
        violators.forEach { frozen[it] = true }
        return violators.isNotEmpty()
    }
}

/**
 * Rounds the shares by their running total, so the sum stays the space shared, and keeps every child
 * within its bounds: a rounding pixel a bound does not allow is left free or overflows instead.
 */
private fun roundWithinBounds(sizes: FloatArray, minSizes: IntArray, maxSizes: IntArray): IntArray {
    var total = 0f
    var roundedTotal = 0
    return IntArray(sizes.size) {
        total += sizes[it]
        val next = total.roundToInt()
        (next - roundedTotal).coerceIn(minSizes[it], maxSizes[it]).also { size -> roundedTotal += size }
    }
}
