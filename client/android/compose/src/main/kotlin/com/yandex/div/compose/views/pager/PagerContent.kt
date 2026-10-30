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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.yandex.div.compose.context.LocalDivViewContext
import com.yandex.div.compose.dagger.DivLocalComponent
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.pager.DivPagerStateStorage
import com.yandex.div.compose.pager.PagerAlignment
import com.yandex.div.compose.pager.PagerLayoutSnapshot
import com.yandex.div.compose.pager.PagerWindowState
import com.yandex.div.compose.pager.mainAxisViewportSize
import com.yandex.div.compose.pager.pagerPosition
import com.yandex.div.compose.pager.rememberAndStoreState
import com.yandex.div.compose.pager.snapOffset
import com.yandex.div.compose.pager.snappedItem
import com.yandex.div.compose.pager.visibleItemSizes
import com.yandex.div.compose.utils.applyIf
import com.yandex.div.compose.utils.isMatchParent
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.compose.utils.scroll.AdjustScrollToItem
import com.yandex.div.compose.utils.scroll.CrossAxisAlignment
import com.yandex.div.compose.utils.scroll.IntrinsicSizeBarrier
import com.yandex.div.compose.utils.scroll.OrientedLazyList
import com.yandex.div.compose.utils.scroll.ScrollableChildItem
import com.yandex.div.compose.utils.scroll.getScrollAxisPaddings
import com.yandex.div.compose.utils.scroll.restrictParentScroll
import com.yandex.div.render.common.pager.PagerItemWindow
import com.yandex.div2.Div
import com.yandex.div2.DivBase
import com.yandex.div2.DivPageTransformation
import com.yandex.div2.DivPager
import com.yandex.div2.DivPagerLayoutMode
import com.yandex.div2.DivSize
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlin.math.abs

@Composable
internal fun PagerContent(
    pager: DivPager,
    items: List<Div>,
    components: Map<Div, DivLocalComponent>?,
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
    val initialSnapPosition = remember { snapPosition }
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
        pager = pager,
        pageCount = items.size,
        listState = listState,
        snapPosition = snapPosition,
        initialPage = initialDefaultItem,
        infiniteScroll = infiniteScroll,
    )

    val alignmentReady = rememberCurrentPageAlignment(
        listState = listState,
        itemWindow = itemWindow,
        itemKeys = itemKeys,
        defaultItem = initialDefaultItem,
        alignment = PagerAlignment(snapPosition, startPadding, endPadding, itemSpacing, pageSize, viewportSize),
    )

    val needsInitialAlignment = pageSize == null && initialSnapPosition != SnapPosition.Start
    var initialPositionAdjusted by remember(listState, needsInitialAlignment) {
        mutableStateOf(!needsInitialAlignment)
    }
    if (needsInitialAlignment) {
        AdjustScrollToItem(listState, rawDefaultItem, initialSnapPosition) {
            initialPositionAdjusted = true
        }
    }
    val selectedActionsHandler = rememberPagerSelectedActionsHandler(
        items = items,
        components = components,
        itemKeys = itemKeys,
        listState = listState,
        snapPosition = snapPosition,
        itemWindow = itemWindow,
        viewportSize = viewportSize,
        enabled = initialPositionAdjusted && alignmentReady,
    )

    val crossAxisSizes = rememberPagerCrossAxisSizes(listState, enabled = !crossAxisBounded)
    val childModifier = childModifier(isHorizontal, viewportSize, crossAxisSizes, pageSize, startPadding, endPadding)
    val transformation = observePageTransformation(pageTransformation)
    val overlap = transformation?.overlap == true
    // Pages of different sizes, overlapped pages and a pager wrapping its pages need their neighbours outside
    // the viewport.
    val neighbourSizes = rememberPagerNeighbourSizes(
        listState, itemKeys, enabled = pageSize == null || overlap || crossAxisSizes != null
    )
    val pageSizePx = pageSize?.let { with(density) { it.roundToPx() } }
    val neighbourSize: (Int) -> Int? = remember(neighbourSizes, pageSizePx) {
        { index -> neighbourSizes?.get(index) ?: pageSizePx }
    }
    val transformationPosition = rememberPageTransformationPosition(
        listState, snapPosition, rawDefaultItem, neighbourSize
    )
    // Read when a gesture settles, so a value change never re-creates the limiter in the middle of a drag.
    val multiPageScroll by rememberUpdatedState(pager.multiPageScroll.observedValue())
    val isParentScrollRestricted = pager.restrictParentScroll.observedValue()
    val gestureSlop = rememberPagerGestureSlop(isParentScrollRestricted)
    val pagingTouchSlopExtra = gestureSlop.pagingTouchSlopExtra
    val swipeLimiter = remember(
        listState, snapPosition, itemKeys, neighbourSize, layoutDirection, pagingTouchSlopExtra
    ) {
        PagerSwipeLimiter(
            listState = listState,
            snapPosition = snapPosition,
            neighbourSize = neighbourSize,
            layoutDirection = layoutDirection,
            pagingTouchSlopExtra = pagingTouchSlopExtra,
            multiPageScroll = { multiPageScroll },
            onTargetPageCalculated = selectedActionsHandler::onTargetPageCalculated,
        )
    }

    val scrollLayout = remember(
        listState, itemWindow, rawDefaultItem, snapPosition, neighbourSize, selectedActionsHandler
    ) {
        PagerScrollLayout(listState, itemWindow, rawDefaultItem, snapPosition, neighbourSize, selectedActionsHandler)
    }
    BindPagerScrollActions(id = pager.id, items = items, layout = scrollLayout)

    CompositionLocalProvider(LocalViewConfiguration provides gestureSlop.listViewConfiguration) {
        OrientedLazyList(
            isHorizontal = isHorizontal,
            modifier = IntrinsicSizeBarrier.fillMaxSize().clipToBounds().trackPagerViewport(neighbourSizes)
                .applyIf(isParentScrollRestricted) { restrictParentScroll(isHorizontal) }
                .pagerSwipe(swipeLimiter)
                .commitPagerCrossAxisSizes(crossAxisSizes),
            listState = listState,
            contentPadding = paddings,
            itemSpacing = itemSpacing,
            crossAxisAlignment = crossAlignment,
            flingBehavior = rememberPagerFlingBehavior(swipeLimiter),
        ) {
            items(
                count = itemWindow.itemCount,
                key = { index -> pageKey(itemKeys, itemWindow, infiniteScroll, index) },
                contentType = { itemWindow },
            ) { index ->
                CompositionLocalProvider(LocalViewConfiguration provides gestureSlop.contentViewConfiguration) {
                    val item = items[itemWindow.realIndex(index)]
                    ScrollableChildItem(
                        item,
                        components?.get(item),
                        childModifier
                            .trackPagerPageSize(neighbourSizes, index, isHorizontal)
                            .pageTransformation(
                                transformation, transformationPosition, index, listState, isHorizontal, layoutDirection
                            ),
                        isHorizontal,
                        crossAlignment,
                        contentModifier = Modifier.trackPagerCrossAxisSize(
                            sizes = crossAxisSizes,
                            isHorizontal = isHorizontal,
                            isMatchParent = item.value().crossAxisSize(isHorizontal).isMatchParent,
                        ),
                    )
                }
            }
        }
    }
}

/** Keys the copies of a page in a cyclic window apart, so each cycle keeps its own composition. */
private fun pageKey(itemKeys: List<Long>, itemWindow: PagerItemWindow, infiniteScroll: Boolean, index: Int): Any {
    val itemKey = itemKeys[itemWindow.realIndex(index)]
    if (!infiniteScroll) return itemKey
    val cycle = (index - itemWindow.edgeItemCount).floorDiv(itemWindow.realItemCount)
    return "$itemKey:$cycle"
}

@Suppress("SwallowedException")
@Composable
private fun rememberCurrentPageAlignment(
    listState: LazyListState,
    itemWindow: PagerItemWindow,
    itemKeys: List<Long>,
    defaultItem: Int,
    alignment: PagerAlignment,
): Boolean {
    val isPositionAvailable by remember(listState) {
        derivedStateOf { listState.layoutInfo.visibleItemsInfo.isNotEmpty() }
    }
    val state = remember(listState) {
        PagerWindowState(
            initialItemWindow = itemWindow,
            initialRealPage = defaultItem,
            initialItemKeys = itemKeys,
            initialAlignment = alignment,
        )
    }
    val density = LocalDensity.current
    val viewportSizePx = with(density) { alignment.viewportSize.roundToPx() }
    var ready by remember(listState, itemWindow, itemKeys, alignment, density) { mutableStateOf(false) }

    LaunchedEffect(listState, itemWindow, itemKeys, alignment, density, isPositionAvailable) {
        if (!isPositionAvailable) return@LaunchedEffect

        val snapPosition = alignment.snapPosition
        suspend fun snapPage(rawPage: Int) {
            try {
                listState.scrollToSnappedPage(rawPage, snapPosition)
            } catch (e: CancellationException) {
                ensureActive()
            }
        }

        snapshotFlow { listState.layoutInfo }.first { alignment.matchesLayout(it, density) }
        val rawPage = listState.pagerPosition(snapPosition).first
        state.update(itemWindow, rawPage, isPositionAvailable, itemKeys, alignment)?.let { snapPage(it) }
        ready = true

        // The closest page of the latest layout in which the list stood still, with the sizes of the visible pages.
        var standing: PagerLayoutSnapshot? = null
        snapshotFlow { listState.layoutSnapshot(snapPosition, viewportSizePx) }
            .filterNotNull()
            .collect { layout ->
                val previous = standing
                if (previous?.page != null && layout.isResizeOf(previous)) {
                    snapPage(previous.page)
                    standing = previous.copy(itemSizes = layout.itemSizes)
                    return@collect
                }
                layout.page?.let { rawPage ->
                    state.update(
                        itemWindow = itemWindow,
                        rawPage = rawPage,
                        isPositionAvailable = listState.layoutInfo.visibleItemsInfo.isNotEmpty(),
                        itemKeys = itemKeys,
                        alignment = alignment,
                    )
                }
                standing = layout.takeIf { it.itemSizes.isNotEmpty() }
            }
    }
    return ready
}

/**
 * The page closest to its snap position with the sizes of the visible pages, which are omitted while the list
 * scrolls, or null while the layout still has a previous viewport size.
 */
private fun LazyListState.layoutSnapshot(snapPosition: SnapPosition, viewportSizePx: Int): PagerLayoutSnapshot? {
    val info = layoutInfo
    if (info.mainAxisViewportSize != viewportSizePx) return null
    val itemSizes = if (isScrollInProgress) emptyList() else info.visibleItemSizes()
    return PagerLayoutSnapshot(info.snappedItem(snapPosition)?.index, itemSizes)
}

private suspend fun LazyListState.scrollToSnappedPage(targetRawPage: Int, snapPosition: SnapPosition) {
    if (layoutInfo.visibleItemsInfo.none { it.index == targetRawPage }) {
        scrollToItem(targetRawPage)
        snapshotFlow { layoutInfo.visibleItemsInfo }
            .first { items -> items.any { it.index == targetRawPage } }
    }

    val info = layoutInfo
    val targetItem = info.visibleItemsInfo.firstOrNull { it.index == targetRawPage } ?: return
    val offset = info.snapOffset(targetItem, snapPosition)
    if (abs(offset) >= 1f) {
        scroll { scrollBy(offset) }
    }
}

@Composable
private fun AdjustScrollToItem(
    listState: LazyListState,
    defaultItem: Int,
    snapPosition: SnapPosition,
    onAdjusted: () -> Unit,
) {
    AdjustScrollToItem(
        listState = listState,
        targetIndex = defaultItem,
        onAdjusted = onAdjusted,
        desiredOffset = { viewportSize, itemSize ->
            val info = listState.layoutInfo
            snapPosition.position(
                layoutSize = viewportSize,
                itemSize = itemSize,
                beforeContentPadding = info.beforeContentPadding,
                afterContentPadding = info.afterContentPadding,
                itemIndex = defaultItem,
                itemCount = info.totalItemsCount,
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
        DivPager.ItemAlignment.CENTER -> ViewportCenterSnapPosition
        DivPager.ItemAlignment.START -> SnapPosition.Start
        DivPager.ItemAlignment.END -> SnapPosition.End
    }

/** Centers a page in the whole viewport, as ViewPager2 does; [SnapPosition.Center] centers it between the paddings. */
private object ViewportCenterSnapPosition : SnapPosition {
    override fun position(
        layoutSize: Int,
        itemSize: Int,
        beforeContentPadding: Int,
        afterContentPadding: Int,
        itemIndex: Int,
        itemCount: Int,
    ): Int = (layoutSize - itemSize) / 2 - beforeContentPadding
}

private fun DivBase.crossAxisSize(isHorizontal: Boolean): DivSize = if (isHorizontal) height else width
