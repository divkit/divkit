package com.yandex.div.compose.views.gallery

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridItemInfo
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalViewConfiguration
import com.yandex.div2.DivGallery
import kotlin.math.abs

@Composable
internal fun rememberGalleryGridFlingBehavior(
    gridState: LazyStaggeredGridState,
    scrollMode: DivGallery.ScrollMode,
    scrollContentAlignment: DivGallery.ContentAlignment,
): FlingBehavior = when (scrollMode) {
    DivGallery.ScrollMode.DEFAULT -> ScrollableDefaults.flingBehavior()
    DivGallery.ScrollMode.PAGING -> {
        val minimumFlingVelocity = LocalViewConfiguration.current.minimumFlingVelocity
        val snapLayoutInfoProvider = remember(gridState, scrollContentAlignment, minimumFlingVelocity) {
            GalleryGridSnapLayoutInfoProvider(gridState, scrollContentAlignment, minimumFlingVelocity)
        }
        rememberSnapFlingBehavior(snapLayoutInfoProvider)
    }
}

private class GalleryGridSnapLayoutInfoProvider(
    private val gridState: LazyStaggeredGridState,
    private val scrollContentAlignment: DivGallery.ContentAlignment,
    private val minimumFlingVelocity: Float,
) : SnapLayoutInfoProvider {
    override fun calculateApproachOffset(velocity: Float, decayOffset: Float): Float = 0f

    override fun calculateSnapOffset(velocity: Float): Float {
        val layoutInfo = gridState.layoutInfo
        val isHorizontal = layoutInfo.orientation == Orientation.Horizontal
        val viewportSize = if (isHorizontal) layoutInfo.viewportSize.width else layoutInfo.viewportSize.height

        fun LazyStaggeredGridItemInfo.mainAxisOffset() = if (isHorizontal) offset.x else offset.y
        fun LazyStaggeredGridItemInfo.mainAxisSize() = if (isHorizontal) size.width else size.height
        fun LazyStaggeredGridItemInfo.snapOffset() = mainAxisOffset() -
            scrollContentAlignment.calculateDesiredScrollOffset(
                viewportSizePx = viewportSize,
                itemSizePx = mainAxisSize(),
                startPaddingPx = layoutInfo.beforeContentPadding,
                endPaddingPx = layoutInfo.afterContentPadding,
            )

        val visibleItems = layoutInfo.visibleItemsInfo.filter { item ->
            item.mainAxisOffset() < layoutInfo.viewportEndOffset &&
                item.mainAxisOffset() + item.mainAxisSize() > layoutInfo.viewportStartOffset
        }
        val target = if (abs(velocity) <= minimumFlingVelocity) {
            visibleItems.minByOrNull { abs(it.snapOffset()) }
        } else {
            val contentEnd = layoutInfo.viewportEndOffset - layoutInfo.afterContentPadding
            val fullyVisibleItems = visibleItems.filter { item ->
                item.mainAxisOffset() >= 0 && item.mainAxisOffset() + item.mainAxisSize() <= contentEnd
            }
            val candidates = fullyVisibleItems.ifEmpty { visibleItems }
            if (velocity < 0) candidates.firstOrNull() else candidates.lastOrNull()
        }
        return target?.snapOffset()?.toFloat() ?: 0f
    }
}
