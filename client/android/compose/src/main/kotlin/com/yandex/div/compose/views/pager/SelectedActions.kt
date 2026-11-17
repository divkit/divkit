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
import com.yandex.div.compose.di.DivLocalComponent
import com.yandex.div.compose.di.LocalComponent
import com.yandex.div.compose.di.handleActions
import com.yandex.div.compose.pager.PagerLayoutSnapshot
import com.yandex.div.compose.pager.mainAxisViewportSize
import com.yandex.div.compose.pager.snappedItem
import com.yandex.div.render.common.pager.PagerItemWindow
import com.yandex.div2.Div
import kotlinx.coroutines.flow.filterNotNull

internal fun interface PagerSelectedActionsHandler {
    fun onTargetPageCalculated(rawIndex: Int)
}

/**
 * Remembers a snap target handler and dispatches queued selections when the list becomes idle.
 * A layout in which a page resized keeps the selection.
 */
@Composable
internal fun rememberPagerSelectedActionsHandler(
    items: List<Div>,
    components: Map<Div, DivLocalComponent>?,
    itemKeys: List<Long>,
    listState: LazyListState,
    snapPosition: SnapPosition,
    itemWindow: PagerItemWindow,
    viewportSize: Dp,
    settledPage: IntState,
    neighbourSizes: PagerNeighbourSizes?,
    enabled: Boolean,
): PagerSelectedActionsHandler {
    val parentComponent = LocalComponent.current
    val isDragged = listState.interactionSource.collectIsDraggedAsState()
    val selection = remember(listState) { PageSelection() }
    val viewportSizePx = with(LocalDensity.current) { viewportSize.roundToPx() }

    LaunchedEffect(
        items, components, itemKeys, listState, snapPosition, itemWindow, parentComponent, enabled,
        viewportSizePx, settledPage, neighbourSizes
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
                    page = listState.snappedPage(snapPosition, itemWindow, settledPage.intValue),
                    resizeCount = neighbourSizes?.resizeCount ?: 0,
                )
            }
        }.filterNotNull().collect { layout ->
            val isResize = layout.isResizeOf(previous)
            previous = layout
            val page = layout.page
            if (page == null || isResize) return@collect
            selection.select(itemKeys[page], items[page])
            while (selection.pendingItems.isNotEmpty()) {
                val item = selection.pendingItems.removeFirst()
                val actions = item.value().selectedActions
                if (actions.isNullOrEmpty()) continue

                val component = components?.get(item) ?: parentComponent
                component.handleActions(actions, DivActionSource.SELECTION)
            }
        }
    }

    val onTargetPageCalculated = rememberUpdatedState<(Int) -> Unit> { rawIndex ->
        if (enabled) {
            listState.realPage(itemWindow, rawIndex)?.let { page ->
                selection.select(itemKeys[page], items[page])
            }
        }
    }
    return remember(listState) {
        PagerSelectedActionsHandler { rawIndex -> onTargetPageCalculated.value(rawIndex) }
    }
}

private class PageSelection {
    private var currentItemKey: Long? = null
    val pendingItems = ArrayDeque<Div>()

    fun select(itemKey: Long, item: Div) {
        if (itemKey == currentItemKey) return
        pendingItems.addLast(item)
        currentItemKey = itemKey
    }
}

private fun LazyListState.snappedPage(
    snapPosition: SnapPosition,
    itemWindow: PagerItemWindow,
    settledPage: Int,
): Int? =
    realPage(itemWindow, layoutInfo.snappedItem(snapPosition, settledPage)?.index)

private fun LazyListState.realPage(itemWindow: PagerItemWindow, rawIndex: Int?): Int? {
    val info = layoutInfo
    if (rawIndex == null || rawIndex !in 0 until info.totalItemsCount) return null
    if (itemWindow.realItemCount == 0 || info.totalItemsCount != itemWindow.itemCount) return null
    // Different cyclic windows can have the same virtual item count.
    if (info.visibleItemsInfo.any { it.contentType != itemWindow }) return null
    return itemWindow.realIndex(rawIndex)
}
