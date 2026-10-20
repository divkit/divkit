package com.yandex.div.compose.views.pager

import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.lazy.LazyLayoutScrollScope
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.LazyListState
import com.yandex.div.compose.pager.mainAxisViewportSize
import com.yandex.div.compose.pager.snapOffset
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

/** Where a pager gesture settles: the pages around the drag origin and the targets of its flings. */
internal class PagerSnapTargets(
    private val listState: LazyListState,
    private val snapPosition: SnapPosition,
    private val neighbourSize: (Int) -> Int?,
) {
    private val delegate = SnapLayoutInfoProvider(listState, snapPosition)

    val viewportSizePx: Int
        get() = listState.layoutInfo.mainAxisViewportSize

    /** The page closest to its snap position in the current layout. */
    fun closestPage(): Int? {
        val info = listState.layoutInfo
        return info.itemsWithNeighbours(neighbourSize).minByOrNull { abs(info.snapOffset(it, snapPosition)) }?.index
    }

    /** Snap offsets around the drag origin [page] in the current layout, which LazyList updates with every scroll. */
    fun dragAnchors(page: Int): DragAnchors? {
        val info = listState.layoutInfo
        val items = info.itemsWithNeighbours(neighbourSize)
        val originItem = items.firstOrNull { it.index == page } ?: return null
        fun snapOffsetOf(index: Int) =
            items.firstOrNull { it.index == index }?.let { info.snapOffset(it, snapPosition) }

        return DragAnchors(
            page = page,
            origin = info.snapOffset(originItem, snapPosition),
            previous = snapOffsetOf(page - 1),
            next = snapOffsetOf(page + 1),
            pageStride = (originItem.size + info.mainAxisItemSpacing).toFloat().coerceAtLeast(1f),
        )
    }

    /** ViewPager2's settling target: the next snap in the direction of [velocity], at most one page from the origin. */
    fun adjacentPage(velocity: Float, anchors: DragAnchors?): SettlingTarget {
        val lower = anchors?.previous ?: Float.NEGATIVE_INFINITY
        val upper = anchors?.next ?: Float.POSITIVE_INFINITY
        val offset = delegate.calculateSnapOffset(velocity).coerceIn(lower, upper)
        val info = listState.layoutInfo
        val items = info.itemsWithNeighbours(neighbourSize)
        val page = items.minByOrNull { abs(info.snapOffset(it, snapPosition) - offset) }
        return SettlingTarget(page?.index, offset, turnsPages = false)
    }

    /**
     * The settling target of a multi-page gesture, as the View renderer's `PagerMultiPageScrollHelper` picks it:
     * a fling turns as many pages as [flingDistance] spans in viewports (`LinearSnapHelper` counts page steps, and a
     * View page spans the viewport); otherwise the page the gesture moves towards ([forward] from the drag [anchors])
     * is the target, so a release never snaps against the gesture.
     *
     * Pages are turned only for a gesture that dragged them ([anchors] of its origin page). Velocity a nested
     * scrollable hands up never moved the pages, and ViewPager2, which does not take part in nested scrolling,
     * leaves the pager where it stands.
     */
    fun multiPageTarget(flingDistance: Float, forward: Boolean?, anchors: DragAnchors?): SettlingTarget {
        val info = listState.layoutInfo
        val items = info.itemsWithNeighbours(neighbourSize)
        val current = items.minByOrNull { abs(info.snapOffset(it, snapPosition)) }
            ?: return SettlingTarget(page = null, offset = 0f, turnsPages = false)
        val lastIndex = info.totalItemsCount - 1
        val pages = (flingDistance / viewportSizePx.coerceAtLeast(1)).roundToInt()
        val flingTarget = (current.index + pages).coerceIn(0, lastIndex)
        if (anchors != null && pages != 0 && flingTarget != current.index) {
            return SettlingTarget(flingTarget, snapOffsetOfPage(flingTarget), turnsPages = true)
        }

        val target = when {
            anchors == null || forward == null -> current.index
            forward -> anchors.page + ceil(anchors.progress).toInt()
            else -> anchors.page + floor(anchors.progress).toInt()
        }.coerceIn(0, lastIndex)
        return SettlingTarget(target, snapOffsetOfPage(target), turnsPages = false)
    }

    /** The scroll distance [offset] in page strides around its target page. */
    fun remainingPageFraction(offset: Float): Float {
        val info = listState.layoutInfo
        val items = info.itemsWithNeighbours(neighbourSize)
        val target = items.minByOrNull { abs(info.snapOffset(it, snapPosition) - offset) } ?: return 0f
        val adjacentIndex = target.index + if (offset < 0f) 1 else -1
        val adjacent = items.firstOrNull { it.index == adjacentIndex }
        val stride = if (adjacent != null) {
            abs(info.snapOffset(target, snapPosition) - info.snapOffset(adjacent, snapPosition))
        } else {
            (target.size + info.mainAxisItemSpacing).toFloat()
        }
        return abs(offset) / stride.coerceAtLeast(1f)
    }

    /**
     * Snap offset of [target], extrapolated page by page past the laid out pages with the last known page size.
     * An extrapolated distance is an estimate: pages of different sizes resolve it once [target] is laid out.
     */
    fun snapOffsetOfPage(target: Int): Float = with(listState.layoutInfo) {
        val items = itemsWithNeighbours(neighbourSize)
        if (items.isEmpty()) 0f else snapOffsetOfPage(items, target)
    }

    /** Whether the layout gives the exact position of [page] rather than an extrapolated one. */
    fun isPositionKnown(page: Int): Boolean =
        listState.layoutInfo.itemsWithNeighbours(neighbourSize).any { it.index == page }

    /**
     * Brings [page] into the layout within the scroll session of [scrollScope], without animation: the page is
     * placed at the start of the list, and [snapOffsetOfPage] then gives the exact distance to its snap position.
     */
    fun layOutPage(scrollScope: ScrollScope, page: Int) {
        LazyLayoutScrollScope(listState, scrollScope).snapToItem(page)
    }

    /** Whether the list can scroll at all in the direction of [offset]; a zero offset has no direction. */
    fun canScrollBy(offset: Float): Boolean = when {
        offset > 0f -> listState.canScrollForward
        offset < 0f -> listState.canScrollBackward
        else -> false
    }

    private fun LazyListLayoutInfo.snapOffsetOfPage(items: List<LazyListItemInfo>, target: Int): Float {
        items.firstOrNull { it.index == target }?.let { return snapOffset(it, snapPosition) }
        val forward = target > items.last().index
        var item = if (forward) items.last() else items.first()
        while (item.index != target) {
            val index = if (forward) item.index + 1 else item.index - 1
            val size = neighbourSize(index) ?: item.size
            val offset = if (forward) {
                item.offset + item.size + mainAxisItemSpacing
            } else {
                item.offset - mainAxisItemSpacing - size
            }
            item = NeighbourPageInfo(index, offset, size)
        }
        return snapOffset(item, snapPosition)
    }
}

/** A settling target: the raw index of its [page], null without laid out pages, the scroll distance to it. */
internal class SettlingTarget(val page: Int?, val offset: Float, val turnsPages: Boolean)

/** Snap offsets of the drag origin and its neighbours; an unknown neighbour leaves its side unbounded. */
internal class DragAnchors(
    val page: Int,
    val origin: Float,
    val previous: Float?,
    val next: Float?,
    pageStride: Float,
) {
    private val previousStride = previous?.let { (origin - it).coerceAtLeast(1f) } ?: pageStride
    private val nextStride = next?.let { (it - origin).coerceAtLeast(1f) } ?: pageStride

    /** Drag progress in pages: -1 at the previous page, 0 at the origin, 1 at the next page. */
    val progress: Float get() = toPages(-origin)

    fun toPages(pixels: Float): Float = pixels / if (pixels < 0f) previousStride else nextStride

    fun toPixels(pages: Float): Float = pages * if (pages < 0f) previousStride else nextStride
}
