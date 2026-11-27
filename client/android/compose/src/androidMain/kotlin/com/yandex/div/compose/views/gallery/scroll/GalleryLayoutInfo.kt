package com.yandex.div.compose.views.gallery.scroll

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import com.yandex.div.render.common.scroll.DivScrollActionUtils
import com.yandex.div2.DivGallery
import kotlin.math.sign

/**
 * Scroll-axis geometry of a laid out gallery item, relative to the content start.
 * Lazy layout offsets grow from the logical start, so horizontal RTL needs no mirroring.
 */
internal data class GalleryLayoutItem(
    val index: Int,
    val offset: Int,
    val size: Int,
)

/**
 * Scroll-axis snapshot of a gallery layout shared by paging snaps, the default item and scroll actions.
 * [items] are in index order; lazy layouts include only the laid out ones.
 */
internal class GalleryLayoutInfo(
    val items: List<GalleryLayoutItem>,
    val itemCount: Int,
    val viewportSize: Int,
    val beforePadding: Int,
    val afterPadding: Int,
    /**
     * The item the scroll position is counted from and the distance scrolled past its start.
     * A lazy staggered grid keeps them while it shows only item spacing and lays out no items.
     */
    val firstItemIndex: Int = items.firstOrNull()?.index ?: -1,
    val firstItemScrollOffset: Int = 0,
) {
    /** Viewport edges relative to the content start. */
    val viewportStart: Int
        get() = -beforePadding
    val viewportEnd: Int
        get() = viewportSize - beforePadding

    fun item(index: Int): GalleryLayoutItem? = items.firstOrNull { it.index == index }

    /** Scroll distance that moves [item] to its [alignment] position. */
    fun snapOffset(item: GalleryLayoutItem, alignment: DivGallery.ContentAlignment): Int =
        item.offset - alignment.calculateDesiredScrollOffset(viewportSize, item.size, beforePadding, afterPadding)

    /** Scroll distance to the content start, known once the first item is laid out. */
    val distanceToStart: Int? by lazy(LazyThreadSafetyMode.NONE) { item(0)?.offset }

    /** Scroll distance to the content end after the longest lane, known once the last item is laid out. */
    val distanceToEnd: Int? by lazy(LazyThreadSafetyMode.NONE) {
        if (item(itemCount - 1) == null) {
            null
        } else {
            items.maxOf { it.offset + it.size } + afterPadding + beforePadding - viewportSize
        }
    }

    /** [distance] limited by the content edges that are laid out. */
    fun coerceToContent(distance: Int): Int = when {
        distance < 0 -> distanceToStart?.let { maxOf(distance, it) } ?: distance
        distance > 0 -> distanceToEnd?.let { minOf(distance, it) } ?: distance
        else -> distance
    }

    /**
     * Index of the item that a [step] of one moves to from the current [alignment] position.
     * Returns -1 without laid out items.
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

internal fun LazyListLayoutInfo.toGalleryLayoutInfo() = GalleryLayoutInfo(
    items = visibleItemsInfo.map { GalleryLayoutItem(it.index, it.offset, it.size) },
    itemCount = totalItemsCount,
    viewportSize = viewportEndOffset - viewportStartOffset,
    beforePadding = beforeContentPadding,
    afterPadding = afterContentPadding,
)

internal fun LazyStaggeredGridState.toGalleryLayoutInfo(): GalleryLayoutInfo {
    val info = layoutInfo
    val isHorizontal = info.orientation == Orientation.Horizontal
    return GalleryLayoutInfo(
        items = info.visibleItemsInfo.map {
            GalleryLayoutItem(
                index = it.index,
                offset = if (isHorizontal) it.offset.x else it.offset.y,
                size = if (isHorizontal) it.size.width else it.size.height,
            )
        },
        itemCount = info.totalItemsCount,
        viewportSize = info.viewportEndOffset - info.viewportStartOffset,
        beforePadding = info.beforeContentPadding,
        afterPadding = info.afterContentPadding,
        firstItemIndex = firstVisibleItemIndex,
        firstItemScrollOffset = firstVisibleItemScrollOffset,
    )
}

/**
 * Item start relative to the content start at which the item sits in this alignment: edge alignments
 * keep the content padding, the center is the one of the whole viewport.
 */
internal fun DivGallery.ContentAlignment.calculateDesiredScrollOffset(
    viewportSizePx: Int,
    itemSizePx: Int,
    startPaddingPx: Int,
    endPaddingPx: Int,
): Int = when (this) {
    DivGallery.ContentAlignment.START -> 0
    DivGallery.ContentAlignment.CENTER -> (viewportSizePx - itemSizePx) / 2 - startPaddingPx
    DivGallery.ContentAlignment.END -> viewportSizePx - endPaddingPx - itemSizePx - startPaddingPx
}
