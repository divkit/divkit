package com.yandex.div.compose.views.pager

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.layout
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateMeasurement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth

/**
 * Natural cross axis sizes of the placed pages: the visible pages and one pinned neighbour on each side.
 * A pager that wraps its cross axis takes the largest one. Pages that the list only prefetched are measured
 * but not placed, so they do not count. Sizes recorded during a layout pass are published by
 * [commitPagerCrossAxisSizes] in the next one, so every page of a pass sees the same [max].
 */
internal class PagerCrossAxisSizes {
    private val sizes = mutableMapOf<CrossAxisSizeNode, Int>()
    private var changeCount by mutableIntStateOf(0)
    private var committedChangeCount = 0

    var max by mutableIntStateOf(0)
        private set
    private var secondMax by mutableIntStateOf(0)
    private var maxNode: CrossAxisSizeNode? = null

    private fun record(node: CrossAxisSizeNode, size: Int) {
        if (sizes[node] == size) return
        sizes[node] = size
        Snapshot.withoutReadObservation { changeCount++ }
    }

    private fun remove(node: CrossAxisSizeNode) {
        if (sizes.remove(node) != null) Snapshot.withoutReadObservation { changeCount++ }
    }

    /** Reads the change count, so that a page resize alone triggers the next commit. */
    fun observeChanges() {
        changeCount
    }

    fun commit() {
        val currentChangeCount = changeCount
        if (currentChangeCount == committedChangeCount) return
        var largest = 0
        var second = 0
        var largestNode: CrossAxisSizeNode? = null
        sizes.forEach { (node, size) ->
            if (size > largest) {
                second = largest
                largest = size
                largestNode = node
            } else if (size > second) {
                second = size
            }
        }
        max = largest
        secondMax = second
        if (largestNode !== maxNode) {
            // The previous largest page may have recorded a size it took from another page; it measures again.
            maxNode?.let { Snapshot.withoutReadObservation { it.remeasureRequests++ } }
            maxNode = largestNode
        }
        committedChangeCount = currentChangeCount
    }

    /** The largest size of the other pages, so that a page stretched to it does not hold the pager at its own size. */
    private fun maxExcluding(node: CrossAxisSizeNode): Int = if (node === maxNode) secondMax else max

    /**
     * Whether a page of [size] stretched to [othersMax] keeps its size. Of pages equal to the largest one only the
     * committed largest page keeps it; otherwise equal match_parent pages would drop their sizes together and
     * take them back in the next pass.
     */
    private fun keepsSize(node: CrossAxisSizeNode, size: Int, othersMax: Int): Boolean =
        size > othersMax || size == othersMax && node === maxNode

    /**
     * Records the page size measured without a cross axis bound, which is its natural size. A match_parent page
     * is stretched to the largest of the other pages, so its size is its own only when it exceeds them; otherwise
     * it records nothing larger than them, which leaves the pager size to the other pages.
     */
    internal class CrossAxisSizeNode(
        private var owner: PagerCrossAxisSizes,
        private var isHorizontal: Boolean,
        private var isMatchParent: Boolean,
    ) : Modifier.Node(), LayoutModifierNode {
        var remeasureRequests by mutableIntStateOf(0)

        override fun onAttach() {
            // A list slot reused for the same page may skip measurement, which would leave the page unrecorded.
            invalidateMeasurement()
        }

        override fun MeasureScope.measure(measurable: Measurable, constraints: Constraints): MeasureResult {
            remeasureRequests
            val isUnbounded = if (isHorizontal) !constraints.hasBoundedHeight else !constraints.hasBoundedWidth
            if (!isUnbounded) {
                val placeable = measurable.measure(constraints)
                return layout(placeable.width, placeable.height) { placeable.place(0, 0) }
            }
            val othersMax = if (isMatchParent) owner.maxExcluding(this@CrossAxisSizeNode) else 0
            val placeable = measurable.measure(constraints.withCrossAxisSize(othersMax, isHorizontal))
            val size = if (isHorizontal) placeable.height else placeable.width
            val recordedSize = if (owner.keepsSize(this@CrossAxisSizeNode, size, othersMax)) size else 0
            return layout(placeable.width, placeable.height) {
                owner.record(this@CrossAxisSizeNode, recordedSize)
                placeable.place(0, 0)
            }
        }

        override fun onDetach() {
            owner.remove(this)
        }

        fun update(owner: PagerCrossAxisSizes, isHorizontal: Boolean, isMatchParent: Boolean) {
            if (this.owner !== owner) this.owner.remove(this)
            this.owner = owner
            this.isHorizontal = isHorizontal
            this.isMatchParent = isMatchParent
        }
    }
}

@Composable
internal fun rememberPagerCrossAxisSizes(listState: LazyListState, enabled: Boolean): PagerCrossAxisSizes? {
    if (!enabled) return null
    return remember(listState) { PagerCrossAxisSizes() }
}

/** Publishes the page sizes recorded while the pager list was laid out. */
internal fun Modifier.commitPagerCrossAxisSizes(sizes: PagerCrossAxisSizes?): Modifier {
    if (sizes == null) return this
    return layout { measurable, constraints ->
        sizes.observeChanges()
        val placeable = measurable.measure(constraints)
        sizes.commit()
        layout(placeable.width, placeable.height) { placeable.place(0, 0) }
    }
}

/** Records the natural cross axis size of a page content; see [PagerCrossAxisSizes.CrossAxisSizeNode]. */
internal fun Modifier.trackPagerCrossAxisSize(
    sizes: PagerCrossAxisSizes?,
    isHorizontal: Boolean,
    isMatchParent: Boolean,
): Modifier {
    if (sizes == null) return this
    return then(CrossAxisSizeElement(sizes, isHorizontal, isMatchParent))
}

/** Constraints with a minimum cross axis size that does not exceed the maximum. */
internal fun Constraints.withCrossAxisSize(min: Int, isHorizontal: Boolean): Constraints =
    if (isHorizontal) copy(minHeight = constrainHeight(min)) else copy(minWidth = constrainWidth(min))

private data class CrossAxisSizeElement(
    val owner: PagerCrossAxisSizes,
    val isHorizontal: Boolean,
    val isMatchParent: Boolean,
) : ModifierNodeElement<PagerCrossAxisSizes.CrossAxisSizeNode>() {
    override fun create() = PagerCrossAxisSizes.CrossAxisSizeNode(owner, isHorizontal, isMatchParent)

    override fun update(node: PagerCrossAxisSizes.CrossAxisSizeNode) = node.update(owner, isHorizontal, isMatchParent)

    override fun InspectorInfo.inspectableProperties() {
        name = "pagerCrossAxisSize"
    }
}
