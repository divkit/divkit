package com.yandex.div.compose.views.pager

import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.yandex.div.compose.context.LocalDivViewContext
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.pager.DivPagerStateStorage
import com.yandex.div.compose.pager.PagerItemWindow
import com.yandex.div.compose.pager.PagerWindowState
import com.yandex.div.compose.pager.pagerPosition
import com.yandex.div.compose.pager.rememberAndStoreState
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.compose.utils.scroll.AdjustScrollToItem
import com.yandex.div.compose.utils.scroll.CrossAxisAlignment
import com.yandex.div.compose.utils.scroll.IntrinsicSizeBarrier
import com.yandex.div.compose.utils.scroll.OrientedLazyList
import com.yandex.div.compose.utils.scroll.ScrollableChildItem
import com.yandex.div.compose.utils.scroll.desiredSnapOffset
import com.yandex.div.compose.utils.scroll.getScrollAxisPaddings
import com.yandex.div2.Div
import com.yandex.div2.DivPageTransformation
import com.yandex.div2.DivPager
import com.yandex.div2.DivPagerLayoutMode
import kotlinx.coroutines.flow.first

@Composable
internal fun PagerContent(
    id: String?,
    items: List<Div>,
    isHorizontal: Boolean,
    itemSpacing: Dp,
    paddings: PaddingValues,
    layoutMode: DivPagerLayoutMode,
    scrollAxisAlignment: DivPager.ItemAlignment,
    crossAxisAlignment: DivPager.ItemAlignment,
    layoutDirection: LayoutDirection,
    defaultItem: Int,
    infiniteScroll: Boolean,
    pageTransformation: DivPageTransformation?,
    viewportSize: Dp,
    crossAxisBounded: Boolean,
    stateStorage: DivPagerStateStorage
) {
    val viewContext = LocalDivViewContext.current
    val itemKeys = remember(items) { items.map { viewContext.compositionKeyStorage.get(it.value()) } }
    val initialDefaultItem = remember { defaultItem }
    val density = LocalDensity.current
    val snapPosition = scrollAxisAlignment.toSnapPosition()
    val crossAlignment = crossAxisAlignment.toCrossAxisAlignment()
    val (startPadding, endPadding) = paddings.getScrollAxisPaddings(isHorizontal, layoutDirection)
    val itemWindow = remember(items.size, infiniteScroll) {
        if (infiniteScroll && items.isNotEmpty()) {
            PagerItemWindow.virtuallyUnbounded(items.size)
        } else {
            PagerItemWindow(items.size)
        }
    }
    val rawDefaultItem = if (items.isEmpty()) 0 else itemWindow.rawIndex(initialDefaultItem)

    val pageSize = layoutMode.observePageSize(scrollAxisAlignment, viewportSize, itemSpacing, startPadding, endPadding)
    val listState = rememberListState(
        rawDefaultItem,
        snapPosition,
        pageSize,
        itemSpacing,
        startPadding,
        endPadding,
        viewportSize,
        itemWindow.itemCount,
    )

    stateStorage.rememberAndStoreState(
        id = id,
        pageCount = items.size,
        listState = listState,
        snapPosition = snapPosition,
        initialPage = initialDefaultItem,
        infiniteScroll = infiniteScroll,
    )

    PreservePageAcrossWindowChanges(
        listState, snapPosition, itemWindow, itemKeys, initialDefaultItem, startPadding, endPadding
    )

    val needsInitialAlignment = pageSize == null && snapPosition != SnapPosition.Start
    var initialPositionAdjusted by remember(listState, needsInitialAlignment) {
        mutableStateOf(!needsInitialAlignment)
    }
    if (needsInitialAlignment) {
        AdjustScrollToItem(listState, rawDefaultItem, snapPosition, endPadding) {
            initialPositionAdjusted = true
        }
    }
    val selectedActionsHandler = rememberPagerSelectedActionsHandler(
        items = items,
        itemKeys = itemKeys,
        listState = listState,
        snapPosition = snapPosition,
        itemWindow = itemWindow,
        enabled = !needsInitialAlignment || initialPositionAdjusted,
    )

    val childModifier = childModifier(
        isHorizontal, viewportSize, crossAxisBounded, listState,
        paddings, pageSize, startPadding, endPadding, layoutDirection, density
    )
    val transformation = observePageTransformation(pageTransformation)
    val overlap = transformation?.overlap == true
    // Pages of different sizes and overlapped pages need their neighbours outside the viewport.
    val neighbourSizes = rememberPagerNeighbourSizes(
        listState, itemKeys, enabled = pageSize == null || overlap
    )
    val pageSizePx = pageSize?.let { with(density) { it.roundToPx() } }
    val neighbourSize: (Int) -> Int? = remember(neighbourSizes, pageSizePx) {
        { index -> neighbourSizes?.get(index) ?: pageSizePx }
    }
    val transformationPosition = rememberPageTransformationPosition(
        listState, snapPosition, rawDefaultItem, neighbourSize
    )
    val swipeLimiter = remember(listState, snapPosition, itemKeys, neighbourSize, layoutDirection) {
        PagerSwipeLimiter(
            listState, snapPosition, neighbourSize, layoutDirection, selectedActionsHandler::onSnapOffsetCalculated
        )
    }

    // The list starts a page drag after the paging touch slop, as ViewPager2 does,
    // while the page content keeps the default touch slop for its own gestures.
    val viewConfiguration = LocalViewConfiguration.current
    val pagerViewConfiguration = rememberPagerViewConfiguration(viewConfiguration)
    CompositionLocalProvider(LocalViewConfiguration provides pagerViewConfiguration) {
        OrientedLazyList(
            isHorizontal = isHorizontal,
            modifier = IntrinsicSizeBarrier.fillMaxSize().clipToBounds().trackPagerViewport(neighbourSizes)
                .pagerSwipe(swipeLimiter),
            listState = listState,
            contentPadding = paddings,
            itemSpacing = itemSpacing,
            crossAxisAlignment = crossAlignment,
            flingBehavior = rememberPagerFlingBehavior(swipeLimiter),
        ) {
            items(
                count = itemWindow.itemCount,
                key = { index ->
                    val itemKey = itemKeys[itemWindow.realIndex(index)]
                    if (infiniteScroll) {
                        val cycle = (index - itemWindow.edgeItemCount).floorDiv(items.size)
                        "$itemKey:$cycle"
                    } else {
                        itemKey
                    }
                },
                contentType = { itemWindow },
            ) { index ->
                CompositionLocalProvider(LocalViewConfiguration provides viewConfiguration) {
                    ScrollableChildItem(
                        items[itemWindow.realIndex(index)],
                        childModifier
                            .trackPagerPageSize(neighbourSizes, index, isHorizontal)
                            .pageTransformation(
                                transformation, transformationPosition, index, listState, isHorizontal, layoutDirection
                            ),
                        isHorizontal,
                        crossAlignment,
                    )
                }
            }
        }
    }
}

@Composable
private fun PreservePageAcrossWindowChanges(
    listState: LazyListState,
    snapPosition: SnapPosition,
    itemWindow: PagerItemWindow,
    itemKeys: List<Long>,
    defaultItem: Int,
    startPadding: Dp,
    endPadding: Dp,
) {
    val isPositionAvailable by remember(listState) {
        derivedStateOf { listState.layoutInfo.visibleItemsInfo.isNotEmpty() }
    }
    val density = LocalDensity.current
    val startPaddingPx = with(density) { startPadding.roundToPx() }
    val endPaddingPx = with(density) { endPadding.roundToPx() }
    val state = remember(listState) {
        PagerWindowState(
            initialItemWindow = itemWindow,
            initialRealPage = defaultItem,
            initialItemKeys = itemKeys,
        )
    }

    LaunchedEffect(
        listState, itemWindow, itemKeys, snapPosition, isPositionAvailable, startPaddingPx, endPaddingPx
    ) {
        if (!isPositionAvailable) return@LaunchedEffect

        val rawPage = listState.pagerPosition(snapPosition).first
        val targetRawPage = state.update(itemWindow, rawPage, isPositionAvailable, itemKeys)

        if (targetRawPage != null) {
            listState.scrollToSnappedPage(
                targetRawPage, snapPosition, startPaddingPx, endPaddingPx
            )
        }

        snapshotFlow { listState.pagerPosition(snapPosition).first }
            .collect { page ->
                state.update(
                    itemWindow = itemWindow,
                    rawPage = page,
                    isPositionAvailable = listState.layoutInfo.visibleItemsInfo.isNotEmpty(),
                    itemKeys = itemKeys,
                )
            }
    }
}

private suspend fun LazyListState.scrollToSnappedPage(
    targetRawPage: Int,
    snapPosition: SnapPosition,
    startPaddingPx: Int,
    endPaddingPx: Int,
) {
    scrollToItem(targetRawPage)
    snapshotFlow { layoutInfo.visibleItemsInfo }
        .first { items -> items.any { it.index == targetRawPage } }

    val info = layoutInfo
    val targetItem = info.visibleItemsInfo.firstOrNull { it.index == targetRawPage } ?: return
    val viewportSize = info.viewportEndOffset - info.viewportStartOffset
    val desiredOffset = desiredSnapOffset(
        snapPosition = snapPosition,
        viewportSizePx = viewportSize,
        itemSizePx = targetItem.size,
        startPaddingPx = startPaddingPx,
        endPaddingPx = endPaddingPx,
    )
    scroll {
        scrollBy((targetItem.offset - desiredOffset).toFloat())
    }
}

@Composable
private fun AdjustScrollToItem(
    listState: LazyListState,
    defaultItem: Int,
    snapPosition: SnapPosition,
    endPadding: Dp,
    onAdjusted: () -> Unit,
) {
    val endPaddingPx = with(LocalDensity.current) { endPadding.roundToPx() }
    AdjustScrollToItem(
        listState = listState,
        targetIndex = defaultItem,
        onAdjusted = onAdjusted,
        desiredOffset = { viewportSize, itemSize ->
            desiredSnapOffset(
                snapPosition = snapPosition,
                viewportSizePx = viewportSize,
                itemSizePx = itemSize,
                startPaddingPx = 0,
                endPaddingPx = endPaddingPx,
            )
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
