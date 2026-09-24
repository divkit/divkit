package com.yandex.div.compose.views.gallery

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridLayoutInfo
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.unit.LayoutDirection
import com.yandex.div.internal.scroll.SnapPositions
import com.yandex.div.internal.scroll.findDirectionalSnapPosition
import com.yandex.div2.DivGallery
import kotlin.math.abs

@Composable
internal fun rememberGalleryListFlingBehavior(
    listState: LazyListState,
    scrollMode: DivGallery.ScrollMode,
    scrollContentAlignment: DivGallery.ContentAlignment,
): FlingBehavior = when (scrollMode) {
    DivGallery.ScrollMode.DEFAULT -> ScrollableDefaults.flingBehavior()
    DivGallery.ScrollMode.PAGING -> {
        val layoutDirection = LocalLayoutDirection.current
        val minimumFlingVelocity = LocalViewConfiguration.current.minimumFlingVelocity
        val provider = remember(listState, scrollContentAlignment, minimumFlingVelocity, layoutDirection) {
            GallerySnapLayoutInfoProvider(
                minimumFlingVelocity = minimumFlingVelocity,
                scrollContentAlignment = scrollContentAlignment,
                isLinearLayout = true,
                layoutInfo = { listState.layoutInfo.toGallerySnapLayoutInfo(layoutDirection) },
                nearestItemProvider = SnapLayoutInfoProvider(listState, scrollContentAlignment.toSnapPosition()),
            )
        }
        rememberSnapFlingBehavior(provider)
    }
}

@Composable
internal fun rememberGalleryGridFlingBehavior(
    gridState: LazyStaggeredGridState,
    scrollMode: DivGallery.ScrollMode,
    scrollContentAlignment: DivGallery.ContentAlignment,
): FlingBehavior = when (scrollMode) {
    DivGallery.ScrollMode.DEFAULT -> ScrollableDefaults.flingBehavior()
    DivGallery.ScrollMode.PAGING -> {
        val minimumFlingVelocity = LocalViewConfiguration.current.minimumFlingVelocity
        val provider = remember(gridState, scrollContentAlignment, minimumFlingVelocity) {
            GallerySnapLayoutInfoProvider(
                minimumFlingVelocity = minimumFlingVelocity,
                scrollContentAlignment = scrollContentAlignment,
                isLinearLayout = false,
                layoutInfo = { gridState.layoutInfo.toGallerySnapLayoutInfo() },
            )
        }
        rememberSnapFlingBehavior(provider)
    }
}

private class GallerySnapLayoutInfoProvider(
    private val minimumFlingVelocity: Float,
    private val scrollContentAlignment: DivGallery.ContentAlignment,
    private val isLinearLayout: Boolean,
    private val layoutInfo: () -> GallerySnapLayoutInfo,
    private val nearestItemProvider: SnapLayoutInfoProvider? = null,
) : SnapLayoutInfoProvider {
    override fun calculateApproachOffset(velocity: Float, decayOffset: Float): Float = 0f

    override fun calculateSnapOffset(velocity: Float): Float {
        val snapToNearest = abs(velocity) <= minimumFlingVelocity
        if (snapToNearest && nearestItemProvider != null) return nearestItemProvider.calculateSnapOffset(0f)

        val layout = layoutInfo()
        val contentEnd = layout.viewportEnd - layout.afterPadding
        val visibleStart = if (isLinearLayout) 0 else layout.viewportStart
        val visibleEnd = if (isLinearLayout) contentEnd else layout.viewportEnd
        val visibleItems = layout.items.filter {
            it.offset - it.spacingBefore < visibleEnd && it.offset + it.size + it.spacingAfter > visibleStart
        }
        val candidates = if (isLinearLayout) layout.items else visibleItems
        val target = if (snapToNearest) {
            visibleItems.minByOrNull { abs(layout.snapOffset(it)) }
        } else {
            val completeStart = if (isLinearLayout) layout.viewportStart else 0
            val completeEnd = if (isLinearLayout) layout.viewportEnd else contentEnd
            val completeItems = candidates.filter { it.offset >= completeStart && it.offset + it.size <= completeEnd }
            val positions = object : SnapPositions {
                override val itemCount = layout.itemCount
                override val isLinearLayout = this@GallerySnapLayoutInfoProvider.isLinearLayout
                override val firstCompletelyVisible = completeItems.firstOrNull()?.index ?: -1
                override val lastCompletelyVisible = completeItems.lastOrNull()?.index ?: -1
                override val firstVisible = visibleItems.firstOrNull()?.index ?: -1
                override val lastVisible = visibleItems.lastOrNull()?.index ?: -1
            }
            val targetIndex = findDirectionalSnapPosition(velocity > 0, positions)
            candidates.firstOrNull { it.index == targetIndex }
        }
        return target?.let { layout.snapOffset(it).toFloat() } ?: nearestItemProvider?.calculateSnapOffset(0f) ?: 0f
    }

    private fun GallerySnapLayoutInfo.snapOffset(item: GallerySnapItemInfo): Int =
        item.offset - scrollContentAlignment.calculateDesiredScrollOffset(
            viewportSizePx = viewportSize,
            itemSizePx = item.size,
            startPaddingPx = beforePadding,
            endPaddingPx = afterPadding,
        )
}

private fun LazyListLayoutInfo.toGallerySnapLayoutInfo(layoutDirection: LayoutDirection): GallerySnapLayoutInfo {
    val isRtl = orientation == Orientation.Horizontal && layoutDirection == LayoutDirection.Rtl
    return GallerySnapLayoutInfo(
        items = visibleItemsInfo.map {
            GallerySnapItemInfo(
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

private fun LazyStaggeredGridLayoutInfo.toGallerySnapLayoutInfo(): GallerySnapLayoutInfo {
    val isHorizontal = orientation == Orientation.Horizontal
    return GallerySnapLayoutInfo(
        items = visibleItemsInfo.map {
            GallerySnapItemInfo(
                index = it.index,
                offset = if (isHorizontal) it.offset.x else it.offset.y,
                size = if (isHorizontal) it.size.width else it.size.height,
            )
        },
        itemCount = totalItemsCount,
        viewportSize = if (isHorizontal) viewportSize.width else viewportSize.height,
        viewportStart = viewportStartOffset,
        viewportEnd = viewportEndOffset,
        beforePadding = beforeContentPadding,
        afterPadding = afterContentPadding,
    )
}

private class GallerySnapLayoutInfo(
    val items: List<GallerySnapItemInfo>,
    val itemCount: Int,
    val viewportSize: Int,
    val viewportStart: Int,
    val viewportEnd: Int,
    val beforePadding: Int,
    val afterPadding: Int,
)

private class GallerySnapItemInfo(
    val index: Int,
    val offset: Int,
    val size: Int,
    val spacingBefore: Int = 0,
    val spacingAfter: Int = 0,
)
