package com.yandex.div.compose.views.pager

import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.compose.utils.scroll.AdjustScrollToItem
import com.yandex.div.compose.utils.scroll.CrossAxisAlignment
import com.yandex.div.compose.utils.scroll.IntrinsicSizeBarrier
import com.yandex.div.compose.utils.scroll.OrientedLazyList
import com.yandex.div.compose.utils.scroll.ScrollableChildItem
import com.yandex.div.compose.utils.scroll.getScrollAxisPaddings
import com.yandex.div2.Div
import com.yandex.div2.DivPager
import com.yandex.div2.DivPagerLayoutMode

@Composable
internal fun PagerContent(
    items: List<Div>,
    isHorizontal: Boolean,
    itemSpacing: Dp,
    paddings: PaddingValues,
    layoutMode: DivPagerLayoutMode,
    scrollAxisAlignment: DivPager.ItemAlignment,
    crossAxisAlignment: DivPager.ItemAlignment,
    layoutDirection: LayoutDirection,
    defaultItem: Int,
    viewportSize: Dp,
    crossAxisBounded: Boolean,
) {
    val density = LocalDensity.current
    val snapPosition = scrollAxisAlignment.toSnapPosition()
    val crossAlignment = crossAxisAlignment.toCrossAxisAlignment()
    val (startPadding, endPadding) = paddings.getScrollAxisPaddings(isHorizontal, layoutDirection)

    val pageSize = layoutMode.observePageSize(scrollAxisAlignment, viewportSize, itemSpacing, startPadding, endPadding)
    val listState = rememberListState(defaultItem, snapPosition, pageSize, itemSpacing, startPadding, endPadding, viewportSize, items.size)

    if (pageSize == null && snapPosition != SnapPosition.Start) {
        AdjustScrollToItem(listState, defaultItem, snapPosition, endPadding)
    }

    val snapProvider = remember(listState, snapPosition) {
        SinglePageSnapLayoutInfoProvider(SnapLayoutInfoProvider(listState, snapPosition))
    }
    val childModifier = childModifier(
        isHorizontal, viewportSize, crossAxisBounded, listState,
        paddings, pageSize, startPadding, endPadding, layoutDirection, density
    )

    OrientedLazyList(
        isHorizontal = isHorizontal,
        modifier = IntrinsicSizeBarrier.fillMaxSize().clipToBounds(),
        listState = listState,
        contentPadding = paddings,
        itemSpacing = itemSpacing,
        crossAxisAlignment = crossAlignment,
        flingBehavior = rememberSnapFlingBehavior(snapProvider),
    ) {
        items(count = items.size) { index ->
            ScrollableChildItem(items[index], childModifier, isHorizontal, crossAlignment)
        }
    }
}

@Composable
private fun AdjustScrollToItem(
    listState: LazyListState,
    defaultItem: Int,
    snapPosition: SnapPosition,
    endPadding: Dp,
) {
    val endPaddingPx = with(LocalDensity.current) { endPadding.roundToPx() }
    AdjustScrollToItem(
        listState = listState,
        targetIndex = defaultItem,
        desiredOffset = { viewportSize, itemSize ->
            desiredSnapOffset(snapPosition, viewportSize, itemSize, endPaddingPx, 0)
        }
    )
}

@Composable
private fun DivPagerLayoutMode.observePageSize(
    alignment: DivPager.ItemAlignment,
    viewportSize: Dp,
    itemSpacing: Dp,
    startPadding: Dp,
    endPadding: Dp,
): Dp? {
    return when (this) {
        is DivPagerLayoutMode.NeighbourPageSize -> {
            val neighbourSize = value.neighbourPageWidth.observedValue() + itemSpacing
            when (alignment) {
                DivPager.ItemAlignment.CENTER ->
                    (viewportSize - neighbourSize * 2).coerceAtLeast(0.dp)
                DivPager.ItemAlignment.START ->
                    (viewportSize - startPadding - neighbourSize).coerceAtLeast(0.dp)
                DivPager.ItemAlignment.END ->
                    (viewportSize - endPadding - neighbourSize).coerceAtLeast(0.dp)
            }
        }
        is DivPagerLayoutMode.PageSize -> {
            val percentage = value.pageWidth.value.observedValue()
            viewportSize * percentage.toFloat() / 100f
        }
        is DivPagerLayoutMode.PageContentSize -> null
    }
}

@Composable
private fun rememberListState(
    defaultItem: Int,
    snapPosition: SnapPosition,
    pageSize: Dp?,
    itemSpacing: Dp,
    startPadding: Dp,
    endPadding: Dp,
    viewportSize: Dp,
    itemsCount: Int
): LazyListState {
    val density = LocalDensity.current
    val initialScroll = remember(defaultItem, snapPosition, pageSize) {
        calculateInitialScroll(
            defaultItem, snapPosition, pageSize, itemSpacing,
            startPadding, endPadding, viewportSize, itemsCount, density
        )
    }

    return rememberLazyListState(
        initialFirstVisibleItemIndex = initialScroll.itemIndex,
        initialFirstVisibleItemScrollOffset = initialScroll.scrollOffset,
    )
}

private fun DivPager.ItemAlignment.toCrossAxisAlignment(): CrossAxisAlignment =
    when (this) {
        DivPager.ItemAlignment.START -> CrossAxisAlignment.START
        DivPager.ItemAlignment.CENTER -> CrossAxisAlignment.CENTER
        DivPager.ItemAlignment.END -> CrossAxisAlignment.END
    }

private fun DivPager.ItemAlignment.toSnapPosition(): SnapPosition =
    when (this) {
        DivPager.ItemAlignment.CENTER -> SnapPosition.Center
        DivPager.ItemAlignment.START -> SnapPosition.Start
        DivPager.ItemAlignment.END -> SnapPosition.End
    }

private class SinglePageSnapLayoutInfoProvider(
    private val delegate: SnapLayoutInfoProvider,
) : SnapLayoutInfoProvider {
    override fun calculateSnapOffset(velocity: Float) = delegate.calculateSnapOffset(velocity)
    override fun calculateApproachOffset(velocity: Float, decayOffset: Float) = 0f
}
