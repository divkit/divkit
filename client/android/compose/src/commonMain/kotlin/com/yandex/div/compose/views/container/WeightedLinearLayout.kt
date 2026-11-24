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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import kotlin.math.max
import kotlin.math.roundToInt

/** The main axis of a linear container and the arrangement of its children along it. */
internal sealed interface LinearAxis {
    val spacing: Dp

    data class Horizontal(val arrangement: Arrangement.Horizontal) : LinearAxis {
        override val spacing: Dp get() = arrangement.spacing
    }

    data class Vertical(val arrangement: Arrangement.Vertical) : LinearAxis {
        override val spacing: Dp get() = arrangement.spacing
    }
}

/**
 * A Row or a Column for a container with match_parent children along its definite main axis. The
 * other children are measured first; when match_parent children have a min_size, constrained
 * children follow their fixed and unconstrained siblings and leave room for it. The match_parent
 * children then share what remains the way flexbox resolves flexible lengths, see
 * [allocateMatchParentSizes]: a Row or a Column sizes a weighted child once by its share, so a
 * min_size would overflow and a max_size would leave a gap. The margins of a match_parent child
 * are reserved before anything is shared, so its share sizes the content. Every child is measured
 * once, at its final size.
 */
@Composable
internal fun WeightedLinearLayout(
    axis: LinearAxis,
    modifier: Modifier,
    content: @Composable () -> Unit,
) {
    Layout(
        content = content,
        modifier = modifier,
        measurePolicy = remember(axis) { WeightedLinearMeasurePolicy(axis) },
    )
}

/**
 * How [WeightedLinearLayout] lays a child out: [alignment] applies across the main axis,
 * [alignByBaseline] replaces it in a row, [spec] marks a match_parent child and [isConstrained] a
 * child that leaves its match_parent siblings their min_size.
 */
internal data class WeightedLinearChild(
    val alignment: Alignment,
    val alignByBaseline: Boolean = false,
    val spec: MatchParentSpec? = null,
    val isConstrained: Boolean = false,
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any = this@WeightedLinearChild
}

/** The share of a match_parent child: [marginsPx] stay outside the share and inside the measured size. */
internal data class MatchParentSpec(val weight: Float, val minSize: Dp, val maxSize: Dp?, val marginsPx: Int)

private val IntrinsicMeasurable.child: WeightedLinearChild?
    get() = parentData as? WeightedLinearChild

private val IntrinsicMeasurable.matchParentSpec: MatchParentSpec?
    get() = child?.spec

private val IntrinsicMeasurable.isConstrained: Boolean
    get() = child?.isConstrained == true

private class WeightedLinearMeasurePolicy(private val axis: LinearAxis) : MeasurePolicy {

    private val isHorizontal = axis is LinearAxis.Horizontal

    override fun MeasureScope.measure(measurables: List<Measurable>, constraints: Constraints): MeasureResult {
        val mainMax = if (isHorizontal) constraints.maxWidth else constraints.maxHeight
        val crossMax = if (isHorizontal) constraints.maxHeight else constraints.maxWidth
        val placeables = arrayOfNulls<Placeable>(measurables.size)
        val specs = measurables.mapNotNull { it.matchParentSpec }
        var fixedSpace = spacingTotal(measurables.size) + specs.sumOf { it.marginsPx }
        val minSizes = specs.sumOf { it.minSize.roundToPx() }

        measurables.forEachUnweighted(constrainedLast = minSizes > 0) { index, measurable ->
            val remaining = remainingSpace(mainMax, fixedSpace, if (measurable.isConstrained) minSizes else 0)
            val placeable = measurable.measure(childConstraints(0, remaining, crossMax))
            placeables[index] = placeable
            fixedSpace += placeable.mainSize
        }

        // Without a bound there is nothing to share: the children keep their min_size.
        val target = when {
            mainMax != Constraints.Infinity -> mainMax
            isHorizontal -> constraints.minWidth
            else -> constraints.minHeight
        }
        val sizes = allocateMatchParentSizes(specs, target - fixedSpace, this)
        var matchParentIndex = 0
        measurables.forEachIndexed { index, measurable ->
            val spec = measurable.matchParentSpec ?: return@forEachIndexed
            val size = sizes[matchParentIndex++] + spec.marginsPx
            placeables[index] = measurable.measure(childConstraints(size, size, crossMax))
        }

        // The two passes above measure every child.
        return placeChildren(placeables.requireNoNulls(), constraints, mainSize = fixedSpace + sizes.sum())
    }

    private fun MeasureScope.placeChildren(
        measured: Array<Placeable>,
        constraints: Constraints,
        mainSize: Int,
    ): MeasureResult {
        val firstBaseline = measured.maxOfOrNull { it.alignedBaseline ?: 0 } ?: 0
        val afterBaseline = measured.maxOfOrNull { placeable ->
            placeable.alignedBaseline?.let { placeable.crossSize - it } ?: 0
        } ?: 0
        // A child may emit no node, e.g. a custom element without a factory.
        val crossSize = max(measured.maxOfOrNull { it.crossSize } ?: 0, firstBaseline + afterBaseline)
        // Overflow starts at the start of the main axis and is cut at its end.
        val width = constraints.constrainWidth(if (isHorizontal) mainSize else crossSize)
        val height = constraints.constrainHeight(if (isHorizontal) crossSize else mainSize)
        val positions = IntArray(measured.size)
        val sizes = IntArray(measured.size) { measured[it].mainSize }
        when (axis) {
            is LinearAxis.Horizontal -> with(axis.arrangement) { arrange(width, sizes, layoutDirection, positions) }
            is LinearAxis.Vertical -> with(axis.arrangement) { arrange(height, sizes, positions) }
        }
        return layout(width, height) {
            measured.forEachIndexed { index, placeable ->
                val child = placeable.parentData as? WeightedLinearChild
                val aligned = child?.alignment?.align(
                    size = IntSize(placeable.width, placeable.height),
                    space = IntSize(width, height),
                    layoutDirection = layoutDirection,
                )
                when {
                    !isHorizontal -> placeable.place(aligned?.x ?: 0, positions[index])
                    // A child without a baseline stays at the top.
                    child?.alignByBaseline == true ->
                        placeable.place(positions[index], placeable.alignedBaseline?.let { firstBaseline - it } ?: 0)
                    else -> placeable.place(positions[index], aligned?.y ?: 0)
                }
            }
        }
    }

    override fun IntrinsicMeasureScope.minIntrinsicWidth(measurables: List<IntrinsicMeasurable>, height: Int): Int =
        if (isHorizontal) {
            mainIntrinsicSize(measurables) { minIntrinsicWidth(height) }
        } else {
            crossIntrinsicSize(measurables, height) { minIntrinsicWidth(it) }
        }

    override fun IntrinsicMeasureScope.maxIntrinsicWidth(measurables: List<IntrinsicMeasurable>, height: Int): Int =
        if (isHorizontal) {
            mainIntrinsicSize(measurables) { maxIntrinsicWidth(height) }
        } else {
            crossIntrinsicSize(measurables, height) { maxIntrinsicWidth(it) }
        }

    override fun IntrinsicMeasureScope.minIntrinsicHeight(measurables: List<IntrinsicMeasurable>, width: Int): Int =
        if (isHorizontal) {
            crossIntrinsicSize(measurables, width) { minIntrinsicHeight(it) }
        } else {
            mainIntrinsicSize(measurables) { minIntrinsicHeight(width) }
        }

    override fun IntrinsicMeasureScope.maxIntrinsicHeight(measurables: List<IntrinsicMeasurable>, width: Int): Int =
        if (isHorizontal) {
            crossIntrinsicSize(measurables, width) { maxIntrinsicHeight(it) }
        } else {
            mainIntrinsicSize(measurables) { maxIntrinsicHeight(width) }
        }

    // The intrinsic sizes follow the Row and Column formulas: the other children and the margins of
    // the match_parent children fix the space, the match_parent children share the rest by weight.
    private fun Density.mainIntrinsicSize(
        measurables: List<IntrinsicMeasurable>,
        intrinsicSize: IntrinsicMeasurable.() -> Int,
    ): Int {
        var fixedSpace = spacingTotal(measurables.size)
        var unit = 0
        var totalWeight = 0f
        measurables.forEach { measurable ->
            val spec = measurable.matchParentSpec
            if (spec == null) {
                fixedSpace += measurable.intrinsicSize()
            } else {
                fixedSpace += spec.marginsPx
                totalWeight += spec.weight
                unit = max(unit, ((measurable.intrinsicSize() - spec.marginsPx) / spec.weight).roundToInt())
            }
        }
        return fixedSpace + (unit * totalWeight).roundToInt()
    }

    private fun Density.crossIntrinsicSize(
        measurables: List<IntrinsicMeasurable>,
        mainSize: Int,
        intrinsicSize: IntrinsicMeasurable.(mainSize: Int) -> Int,
    ): Int {
        val specs = measurables.mapNotNull { it.matchParentSpec }
        val minSizes = specs.sumOf { it.minSize.roundToPx() }
        var fixedSpace = spacingTotal(measurables.size) + specs.sumOf { it.marginsPx }
        var crossSize = 0
        measurables.forEachUnweighted(constrainedLast = minSizes > 0) { _, measurable ->
            val remaining = remainingSpace(mainSize, fixedSpace, if (measurable.isConstrained) minSizes else 0)
            val contentSize = if (isHorizontal) {
                measurable.maxIntrinsicWidth(Constraints.Infinity)
            } else {
                measurable.maxIntrinsicHeight(Constraints.Infinity)
            }
            val childSize = contentSize.coerceAtMost(remaining)
            fixedSpace += childSize
            crossSize = max(crossSize, measurable.intrinsicSize(childSize))
        }
        val sizes = if (mainSize == Constraints.Infinity) {
            null
        } else {
            allocateMatchParentSizes(specs, mainSize - fixedSpace, this)
        }
        var matchParentIndex = 0
        measurables.forEach { measurable ->
            val spec = measurable.matchParentSpec ?: return@forEach
            val childSize = sizes?.let { it[matchParentIndex++] + spec.marginsPx } ?: Constraints.Infinity
            crossSize = max(crossSize, measurable.intrinsicSize(childSize))
        }
        return crossSize
    }

    private fun childConstraints(mainMin: Int, mainMax: Int, crossMax: Int): Constraints = if (isHorizontal) {
        Constraints(minWidth = mainMin, maxWidth = mainMax, maxHeight = crossMax)
    } else {
        Constraints(maxWidth = crossMax, minHeight = mainMin, maxHeight = mainMax)
    }

    private val Placeable.mainSize: Int
        get() = if (isHorizontal) width else height

    private val Placeable.crossSize: Int
        get() = if (isHorizontal) height else width

    private val Placeable.alignedBaseline: Int?
        get() = if ((parentData as? WeightedLinearChild)?.alignByBaseline == true) {
            get(FirstBaseline).takeIf { it != AlignmentLine.Unspecified }
        } else {
            null
        }

    private fun Density.spacingTotal(childCount: Int): Int =
        axis.spacing.roundToPx() * (childCount - 1).coerceAtLeast(0)
}

/** What is left of [mainMax] for a child measured after [fixedSpace] is taken and [reserved] is kept. */
private fun remainingSpace(mainMax: Int, fixedSpace: Int, reserved: Int): Int =
    if (mainMax == Constraints.Infinity) Constraints.Infinity else (mainMax - fixedSpace - reserved).coerceAtLeast(0)

private inline fun <T : IntrinsicMeasurable> List<T>.forEachUnweighted(
    constrainedLast: Boolean,
    action: (Int, T) -> Unit,
) {
    forEachIndexed { index, measurable ->
        if (measurable.matchParentSpec == null && (!constrainedLast || !measurable.isConstrained)) {
            action(index, measurable)
        }
    }
    if (constrainedLast) {
        forEachIndexed { index, measurable ->
            if (measurable.matchParentSpec == null && measurable.isConstrained) action(index, measurable)
        }
    }
}

/**
 * Shares [space] among [specs] by weight, resolving min_size and max_size the way flexbox resolves
 * flexible lengths: each round every child not yet frozen takes its share clamped to its bounds;
 * if the clamps add space, the children held at min_size are frozen, if they take space, those held
 * at max_size are, and the rest share again. Without space to share every child keeps its min_size.
 */
internal fun allocateMatchParentSizes(specs: List<MatchParentSpec>, space: Int, density: Density): IntArray {
    val minSizes = IntArray(specs.size) { with(density) { specs[it].minSize.roundToPx() } }
    if (space <= 0) return minSizes
    val maxSizes = IntArray(specs.size) { with(density) { specs[it].maxSize?.roundToPx() ?: Int.MAX_VALUE } }
    val flexible = FlexibleSizes(FloatArray(specs.size) { specs[it].weight }, minSizes, maxSizes, space)
    while (flexible.shareRound()) Unit
    return roundWithinBounds(flexible.sizes, minSizes, maxSizes)
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
