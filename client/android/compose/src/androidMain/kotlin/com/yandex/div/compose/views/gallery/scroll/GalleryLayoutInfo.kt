package com.yandex.div.compose.views.gallery.scroll

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridLayoutInfo
import androidx.compose.ui.unit.LayoutDirection
import com.yandex.div.compose.utils.scroll.desiredSnapOffset
import com.yandex.div.render.common.scroll.DivScrollActionUtils
import com.yandex.div2.DivGallery
import kotlin.math.sign

/**
 * Scroll-axis geometry of a laid out gallery item, relative to the content start.
 * Lazy layout offsets grow from the logical start, so horizontal RTL needs no mirroring.
 */
internal class GalleryLayoutItem(
    val index: Int,
    val offset: Int,
    val size: Int,
    /** Item spacing attributed to the item by paging snaps; scroll actions ignore it. */
    val spacingBefore: Int = 0,
    val spacingAfter: Int = 0,
)

/**
 * Scroll-axis snapshot of a gallery layout shared by paging snaps, nearest-item search and scroll actions.
 * [items] are in index order; lazy layouts include only the laid out ones.
 */
internal class GalleryLayoutInfo(
    val items: List<GalleryLayoutItem>,
    val itemCount: Int,
    val viewportSize: Int,
    val viewportStart: Int,
    val viewportEnd: Int,
    val beforePadding: Int,
    val afterPadding: Int,
) {
    fun item(index: Int): GalleryLayoutItem? = items.firstOrNull { it.index == index }

    /** Scroll distance that moves [item] to its [alignment] position. */
    fun snapOffset(item: GalleryLayoutItem, alignment: DivGallery.ContentAlignment): Int =
        item.offset - alignment.calculateDesiredScrollOffset(viewportSize, item.size, beforePadding, afterPadding)

    /** Scroll distance to the content start, known once the first item is laid out. */
    fun distanceToStart(): Int? = item(0)?.offset

    /** Scroll distance to the content end after the longest lane, known once the last item is laid out. */
    fun distanceToEnd(): Int? {
        if (item(itemCount - 1) == null) return null
        return items.maxOf { it.offset + it.size } + afterPadding - viewportEnd
    }

    /**
     * Index of the item that a [step] of one moves to from the current [alignment] position,
     * like DivLinearLayoutManager.getNearestItemPosition. Returns -1 without laid out items.
     */
    fun nearestItem(step: Int, alignment: DivGallery.ContentAlignment): Int {
        val ordered = if (step > 0) items else items.asReversed()
        for (item in ordered) {
            val indexOffset = DivScrollActionUtils.nearestItemIndexOffset(
                snapOffset(item, alignment), 0, step, reversed = false,
            ) ?: continue
            return item.index + indexOffset
        }
        return ordered.lastOrNull()?.let { it.index + step.sign } ?: -1
    }
}

internal fun LazyListLayoutInfo.toGalleryLayoutInfo(
    layoutDirection: LayoutDirection = LayoutDirection.Ltr,
): GalleryLayoutInfo {
    val isRtl = orientation == Orientation.Horizontal && layoutDirection == LayoutDirection.Rtl
    return GalleryLayoutInfo(
        items = visibleItemsInfo.map {
            GalleryLayoutItem(
                index = it.index,
                offset = it.offset,
                size = it.size,
                spacingBefore = if (isRtl && it.index > 0) mainAxisItemSpacing else 0,
                spacingAfter = if (!isRtl && it.index < totalItemsCount - 1) mainAxisItemSpacing else 0,
            )
        },
        itemCount = totalItemsCount,
        viewportSize = viewportEndOffset - viewportStartOffset,
        viewportStart = viewportStartOffset,
        viewportEnd = viewportEndOffset,
        beforePadding = beforeContentPadding,
        afterPadding = afterContentPadding,
    )
}

internal fun LazyStaggeredGridLayoutInfo.toGalleryLayoutInfo(): GalleryLayoutInfo {
    val isHorizontal = orientation == Orientation.Horizontal
    return GalleryLayoutInfo(
        items = visibleItemsInfo.map {
            GalleryLayoutItem(
                index = it.index,
                offset = if (isHorizontal) it.offset.x else it.offset.y,
                size = if (isHorizontal) it.size.width else it.size.height,
            )
        },
        itemCount = totalItemsCount,
        viewportSize = viewportEndOffset - viewportStartOffset,
        viewportStart = viewportStartOffset,
        viewportEnd = viewportEndOffset,
        beforePadding = beforeContentPadding,
        afterPadding = afterContentPadding,
    )
}

/** Item start relative to the content start at which the item sits in this alignment. */
internal fun DivGallery.ContentAlignment.calculateDesiredScrollOffset(
    viewportSizePx: Int,
    itemSizePx: Int,
    startPaddingPx: Int,
    endPaddingPx: Int,
): Int {
    return desiredSnapOffset(
        snapPosition = when (this) {
            DivGallery.ContentAlignment.START -> SnapPosition.Start
            DivGallery.ContentAlignment.CENTER -> SnapPosition.Center
            DivGallery.ContentAlignment.END -> SnapPosition.End
        },
        viewportSizePx = viewportSizePx,
        itemSizePx = itemSizePx,
        startPaddingPx = startPaddingPx,
        endPaddingPx = endPaddingPx,
    ) - startPaddingPx
}
