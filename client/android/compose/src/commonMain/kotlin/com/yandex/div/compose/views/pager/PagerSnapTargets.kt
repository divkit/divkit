package com.yandex.div.compose.views.pager

import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.lazy.LazyLayoutScrollScope
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.LazyListState
import com.yandex.div.compose.pager.closestItem
import com.yandex.div.compose.pager.mainAxisViewportSize
import com.yandex.div.compose.pager.reachableSnapOffset
import com.yandex.div.compose.pager.snappedItem
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

/** Where a pager gesture settles: the pages around the drag origin and the targets of its flings. */
internal class PagerSnapTargets(
    private val listState: LazyListState,
    private val snapPosition: SnapPosition,
    private val neighbourSize: (Int) -> Int?,
    private val settledPage: () -> Int,
) {
    val viewportSizePx: Int
        get() = listState.layoutInfo.mainAxisViewportSize

    /** The page the current layout stands on. */
    fun closestPage(): Int? {
        val info = listState.layoutInfo
        return info.snappedItem(snapPosition, settledPage(), info.itemsWithNeighbours(neighbourSize))?.index
    }

    /**
     * Snap offsets around the drag origin [page] in the current layout, which LazyList updates with every scroll,
     * and the drag progress towards a page sharing the origin's snap position, [tiedProgress], which it does not see.
     */
    fun dragAnchors(page: Int, tiedProgress: Float = 0f): DragAnchors? {
        val info = listState.layoutInfo
        val items = info.itemsWithNeighbours(neighbourSize)
        val originItem = items.firstOrNull { it.index == page } ?: return null
        fun snapOffsetOf(index: Int) =
            items.firstOrNull { it.index == index }?.let { info.reachableSnapOffset(it, snapPosition, items) }

        return DragAnchors(
            page = page,
            origin = info.reachableSnapOffset(originItem, snapPosition, items),
            previous = snapOffsetOf(page - 1),
            next = snapOffsetOf(page + 1),
            pageStride = (originItem.size + info.mainAxisItemSpacing).toFloat().coerceAtLeast(1f),
            tiedProgress = tiedProgress,
        )
    }

    /**
     * ViewPager2's settling target: the next snap in the direction of [velocity], at most one page from the origin.
     * Of the pages that share the snap position at a list edge, a gesture that dragged the pages ([anchors] of its
     * origin page) targets the one next to the origin in that direction, or without velocity the one it dragged
     * more than half a page towards; otherwise the target is the one closest to the page the pager stands on.
     */
    fun adjacentPage(velocity: Float, anchors: DragAnchors?): SettlingTarget {
        val lower = anchors?.previous ?: Float.NEGATIVE_INFINITY
        val upper = anchors?.next ?: Float.POSITIVE_INFINITY
        val info = listState.layoutInfo
        val items = info.itemsWithNeighbours(neighbourSize)
        val offset = info.snapOffsetTowards(velocity, items).coerceIn(lower, upper)
        val towards = when {
            anchors == null -> settledPage()
            velocity > 0f -> anchors.page + 1
            velocity < 0f -> anchors.page - 1
            else -> anchors.page + anchors.progress.roundToInt()
        }
        val page = info.closestItem(snapPosition, towards, items, offset)
        return SettlingTarget(page?.index, offset, turnsPages = false)
    }

    /**
     * The scroll distance to the snap position the visible pages reach next in the direction of [velocity], or to
     * the closest one without velocity, as LazyList's SnapLayoutInfoProvider picks it.
     */
    private fun LazyListLayoutInfo.snapOffsetTowards(velocity: Float, items: List<LazyListItemInfo>): Float {
        val offsets = visibleItemsInfo.map { reachableSnapOffset(it, snapPosition, items) }
        val lower = offsets.filter { it <= 0f }.maxOrNull()
        val upper = offsets.filter { it >= 0f }.minOrNull()
        val target = when {
            velocity > 0f -> upper
            velocity < 0f -> lower
            lower == null || upper != null && abs(upper) <= abs(lower) -> upper
            else -> lower
        }
        return target ?: 0f
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
        val current = info.snappedItem(snapPosition, settledPage(), items)
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
        val target = info.closestItem(snapPosition, settledPage(), items, offset) ?: return 0f
        val adjacentIndex = target.index + if (offset < 0f) 1 else -1
        val adjacent = items.firstOrNull { it.index == adjacentIndex }
        val stride = if (adjacent != null) {
            abs(
                info.reachableSnapOffset(target, snapPosition, items) -
                    info.reachableSnapOffset(adjacent, snapPosition, items)
            )
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
        items.firstOrNull { it.index == target }?.let { return reachableSnapOffset(it, snapPosition, items) }
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
        return reachableSnapOffset(item, snapPosition, items)
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
    /** Drag progress in pages towards a neighbour that shares the origin's snap position, see [scrollableProgress]. */
    private val tiedProgress: Float = 0f,
) {
    private val previousStride = previous?.let { (origin - it).coerceAtLeast(1f) } ?: pageStride
    private val nextStride = next?.let { (it - origin).coerceAtLeast(1f) } ?: pageStride

    /** Drag progress in pages: -1 at the previous page, 0 at the origin, 1 at the next page. */
    val progress: Float get() = toPages(-origin) + tiedProgress

    /**
     * The part of drag [progress] the list scrolls: none of it towards a neighbour that shares the origin's snap
     * position at a list edge, since the list cannot scroll there.
     */
    fun scrollableProgress(progress: Float): Float = when {
        progress < 0f && previous == origin -> 0f
        progress > 0f && next == origin -> 0f
        else -> progress
    }

    fun toPages(pixels: Float): Float = pixels / if (pixels < 0f) previousStride else nextStride

    fun toPixels(pages: Float): Float = pages * if (pages < 0f) previousStride else nextStride
}
