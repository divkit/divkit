package com.yandex.div.compose.views.pager

import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.IntState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.yandex.div.compose.actions.DivActionSource
import com.yandex.div.compose.collection.VisibleCollectionItems
import com.yandex.div.compose.di.LocalComponent
import com.yandex.div.compose.di.handleActions
import com.yandex.div.compose.pager.PagerLayoutSnapshot
import com.yandex.div.compose.pager.mainAxisViewportSize
import com.yandex.div.compose.pager.snappedItem
import com.yandex.div.compose.views.tabs.LocalIsMeasurementPass
import com.yandex.div.render.common.pager.PagerItemWindow
import com.yandex.div2.Div
import kotlinx.coroutines.flow.filterNotNull

internal fun interface PagerSelectedActionsHandler {
    fun onTargetPageCalculated(rawIndex: Int)
}

/** Receives every page the pager settles on, starting with the initial one. */
internal fun interface PagerPageListener {
    fun onPageSettled(itemWindow: PagerItemWindow, rawPage: Int)
}

/**
 * Remembers a snap target handler and dispatches queued selections when the list becomes idle.
 * A layout in which a page resized keeps the selection.
 */
@Composable
internal fun rememberPagerSelectedActionsHandler(
    collection: VisibleCollectionItems,
    listState: LazyListState,
    snapPosition: SnapPosition,
    itemWindow: PagerItemWindow,
    viewportSize: Dp,
    settledPage: IntState,
    neighbourSizes: PagerNeighbourSizes?,
    enabled: Boolean,
    pageListener: PagerPageListener? = null,
): PagerSelectedActionsHandler {
    if (LocalIsMeasurementPass.current) return PagerSelectedActionsHandler {}
    val items = collection.items
    val parentComponent = LocalComponent.current
    val isDragged = listState.interactionSource.collectIsDraggedAsState()
    val selection = remember(listState) { PageSelection() }
    val viewportSizePx = with(LocalDensity.current) { viewportSize.roundToPx() }

    LaunchedEffect(
        collection, listState, snapPosition, itemWindow, parentComponent, enabled,
        viewportSizePx, settledPage, neighbourSizes, pageListener,
    ) {
        if (!enabled) return@LaunchedEffect

        var previous = PagerLayoutSnapshot(page = null, resizeCount = null)
        snapshotFlow {
            when {
                // The layout still has the previous viewport size and tells nothing about the page: skip it.
                listState.layoutInfo.mainAxisViewportSize != viewportSizePx -> null
                // A moving list selects nothing: a gesture or a scroll action selects its target page when it is
                // calculated. Without a resize count, the first standing layout after the scroll is never a resize.
                isDragged.value || listState.isScrollInProgress -> PagerLayoutSnapshot(page = null, resizeCount = null)
                // The list stands still: the page it stands on is selected, unless a page resized since the previous
                // layout, in which the list stood still too (see isResizeOf), and the selection stays.
                else -> PagerLayoutSnapshot(
                    page = listState.snappedRawPage(snapPosition, itemWindow, settledPage.intValue),
                    resizeCount = neighbourSizes?.resizeCount ?: 0,
                )
            }
        }.filterNotNull().collect { layout ->
            val isResize = layout.isResizeOf(previous)
            previous = layout
            val rawPage = layout.page
            if (rawPage == null || isResize) return@collect
            val page = itemWindow.realIndex(rawPage)
            selection.select(page, items[page])
            pageListener?.onPageSettled(itemWindow, rawPage)
            while (selection.pendingItems.isNotEmpty()) {
                val item = selection.pendingItems.removeFirst()
                val actions = item.value().selectedActions
                if (actions.isNullOrEmpty()) continue

                val component = collection.components?.get(item) ?: parentComponent
                component.handleActions(actions, DivActionSource.SELECTION)
            }
        }
    }

    val onTargetPageCalculated = rememberUpdatedState<(Int) -> Unit> { rawIndex ->
        if (enabled) {
            listState.validRawPage(itemWindow, rawIndex)?.let { rawPage ->
                val page = itemWindow.realIndex(rawPage)
                selection.select(page, items[page])
            }
        }
    }
    return remember(listState) {
        PagerSelectedActionsHandler { rawIndex -> onTargetPageCalculated.value(rawIndex) }
    }
}

/** Selects a page by its position: a page that takes the current position after a change of pages is not selected. */
private class PageSelection {
    private var currentPage: Int? = null
    val pendingItems = ArrayDeque<Div>()

    fun select(page: Int, item: Div) {
        if (page == currentPage) return
        pendingItems.addLast(item)
        currentPage = page
    }
}

private fun LazyListState.snappedRawPage(
    snapPosition: SnapPosition,
    itemWindow: PagerItemWindow,
    settledPage: Int,
): Int? =
    validRawPage(itemWindow, layoutInfo.snappedItem(snapPosition, settledPage)?.index)

private fun LazyListState.validRawPage(itemWindow: PagerItemWindow, rawIndex: Int?): Int? {
    val info = layoutInfo
    if (rawIndex == null || rawIndex !in 0 until info.totalItemsCount) return null
    if (itemWindow.realItemCount == 0 || info.totalItemsCount != itemWindow.itemCount) return null
    // Different cyclic windows can have the same virtual item count.
    if (info.visibleItemsInfo.any { it.contentType != itemWindow }) return null
    return rawIndex
}
