package com.yandex.div.compose.views.pager

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.BeyondBoundsLayout
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.LocalPinnableContainer
import androidx.compose.ui.layout.PinnableContainer
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.node.GlobalPositionAwareModifierNode
import androidx.compose.ui.node.LayoutAwareModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.findNearestBeyondBoundsLayoutAncestor
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.IntSize

@Composable
internal fun rememberPagerNeighbourSizes(
    listState: LazyListState,
    itemKeys: List<Long>,
    enabled: Boolean,
): PagerNeighbourSizes? {
    if (!enabled) return null
    val sizes = remember(listState, itemKeys) { PagerNeighbourSizes(listState) }
    DisposableEffect(sizes) {
        onDispose { sizes.dispose() }
    }
    return sizes
}

@Composable
internal fun Modifier.trackPagerPageSize(
    neighbourSizes: PagerNeighbourSizes?,
    index: Int,
    isHorizontal: Boolean,
): Modifier {
    if (neighbourSizes == null) return this
    return then(PageSizeElement(neighbourSizes, index, isHorizontal, LocalPinnableContainer.current))
}

internal fun Modifier.trackPagerViewport(neighbourSizes: PagerNeighbourSizes?): Modifier {
    if (neighbourSizes == null) return this
    return onGloballyPositioned { coordinates ->
        neighbourSizes.setViewportVisible(!coordinates.boundsInWindow().isEmpty)
    }
}

internal class PagerNeighbourSizes(private val listState: LazyListState) {
    private val sizes = mutableStateMapOf<Int, Int>()
    private val pages = mutableMapOf<Int, PageSizeNode>()
    private var visibleRange: IntRange? = null
    private var updating = false
    private var viewportVisible = false

    operator fun get(index: Int): Int? = sizes[index]

    fun setViewportVisible(visible: Boolean) {
        if (viewportVisible == visible) return
        viewportVisible = visible
        visibleRange = null
        if (!visible) pages.values.toList().forEach { it.release() }
    }

    fun dispose() {
        pages.values.toList().forEach { it.release() }
        pages.clear()
        sizes.clear()
        visibleRange = null
    }

    private fun register(page: PageSizeNode) {
        pages.put(page.index, page)?.release()
        visibleRange = null
    }

    private fun unregister(page: PageSizeNode) {
        page.release()
        if (pages[page.index] !== page) return
        pages.remove(page.index)
        sizes.remove(page.index)
        visibleRange = null
    }

    private fun updateNeighbours(layout: BeyondBoundsLayout) {
        if (updating || !viewportVisible) return
        val info = listState.layoutInfo
        val visible = info.visibleItemsInfo
        if (visible.isEmpty()) return
        val range = visible.first().index..visible.last().index
        if (visibleRange == range) return

        updating = true
        try {
            val before = (range.first - 1).takeIf { it >= 0 }
            val after = (range.last + 1).takeIf { it < info.totalItemsCount }
            pages.values.toList().forEach { page ->
                if (page.index != before && page.index != after) page.release()
            }
            pinNeighbour(before, BeyondBoundsLayout.LayoutDirection.Before, layout)
            pinNeighbour(after, BeyondBoundsLayout.LayoutDirection.After, layout)
            val beforePinned = before == null || pages[before]?.isPinned == true
            val afterPinned = after == null || pages[after]?.isPinned == true
            if (beforePinned && afterPinned) {
                visibleRange = range
            }
        } finally {
            updating = false
        }
    }

    private fun pinNeighbour(index: Int?, direction: BeyondBoundsLayout.LayoutDirection, layout: BeyondBoundsLayout) {
        if (index == null) return
        if (pages[index]?.isPinned == true) return
        // Lays out pages beyond the viewport until the neighbour is composed, then keeps it composed.
        layout.layout(direction) { pages[index]?.pin() }
    }

    internal class PageSizeNode(
        private var owner: PagerNeighbourSizes,
        var index: Int,
        private var isHorizontal: Boolean,
        private var container: PinnableContainer?,
    ) : Modifier.Node(), LayoutAwareModifierNode, GlobalPositionAwareModifierNode {
        private var handle: PinnableContainer.PinnedHandle? = null
        private var size: IntSize? = null
        val isPinned: Boolean get() = handle != null

        override fun onAttach() {
            owner.register(this)
        }

        override fun onDetach() {
            owner.unregister(this)
        }

        override fun onRemeasured(size: IntSize) {
            this.size = size
            owner.sizes[index] = if (isHorizontal) size.width else size.height
        }

        override fun onGloballyPositioned(coordinates: LayoutCoordinates) {
            val layout = findNearestBeyondBoundsLayoutAncestor() ?: return
            owner.updateNeighbours(layout)
        }

        fun update(
            owner: PagerNeighbourSizes,
            index: Int,
            isHorizontal: Boolean,
            container: PinnableContainer?,
        ) {
            val registrationChanged = this.owner !== owner || this.index != index || this.container !== container
            if (registrationChanged && isAttached) this.owner.unregister(this)
            this.owner = owner
            this.index = index
            this.isHorizontal = isHorizontal
            this.container = container
            if (registrationChanged && isAttached) owner.register(this)
            size?.let(::onRemeasured)
        }

        fun pin(): Boolean {
            if (handle == null) handle = container?.pin()
            return isPinned
        }

        fun release() {
            handle?.release()
            handle = null
        }
    }
}

private data class PageSizeElement(
    val owner: PagerNeighbourSizes,
    val index: Int,
    val isHorizontal: Boolean,
    val container: PinnableContainer?,
) : ModifierNodeElement<PagerNeighbourSizes.PageSizeNode>() {
    override fun create() = PagerNeighbourSizes.PageSizeNode(owner, index, isHorizontal, container)

    override fun update(node: PagerNeighbourSizes.PageSizeNode) = node.update(owner, index, isHorizontal, container)

    override fun InspectorInfo.inspectableProperties() {
        name = "pagerPageSize"
    }
}
