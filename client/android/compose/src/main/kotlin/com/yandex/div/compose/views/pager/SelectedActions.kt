package com.yandex.div.compose.views.pager

import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import com.yandex.div.compose.actions.DivActionSource
import com.yandex.div.compose.context.LocalDivViewContext
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.dagger.handleActions
import com.yandex.div.compose.pager.PagerItemWindow
import com.yandex.div2.Div
import kotlinx.coroutines.flow.filterNotNull
import kotlin.math.abs

internal fun interface PagerSelectedActionsHandler {
    fun onSnapOffsetCalculated(offset: Float)
}

/** Remembers a snap target handler and dispatches queued selections when the list becomes idle. */
@Composable
internal fun rememberPagerSelectedActionsHandler(
    items: List<Div>,
    itemKeys: List<Long>,
    listState: LazyListState,
    snapPosition: SnapPosition,
    itemWindow: PagerItemWindow,
    enabled: Boolean,
): PagerSelectedActionsHandler {
    val viewContext = LocalDivViewContext.current
    val parentComponent = LocalComponent.current
    val isDragged = listState.interactionSource.collectIsDraggedAsState()
    val selection = remember(listState) { PageSelection() }

    LaunchedEffect(items, itemKeys, listState, snapPosition, itemWindow, viewContext, parentComponent, enabled) {
        if (!enabled) return@LaunchedEffect

        snapshotFlow {
            if (isDragged.value || listState.isScrollInProgress) {
                null
            } else {
                listState.selectedPage(snapPosition, itemWindow, 0f)
            }
        }.filterNotNull().collect { page ->
            selection.select(itemKeys[page], items[page])
            while (selection.pendingItems.isNotEmpty()) {
                val item = selection.pendingItems.removeFirst().value()
                val actions = item.selectedActions
                if (actions.isNullOrEmpty()) continue

                val component = if (item.functions.isNullOrEmpty() &&
                    item.variables.isNullOrEmpty() && item.variableTriggers.isNullOrEmpty()
                ) {
                    parentComponent
                } else {
                    viewContext.getLocalComponent(item, parentComponent)
                }
                component.handleActions(actions, DivActionSource.SELECTION)
            }
        }
    }

    val onSnapOffsetCalculated = rememberUpdatedState<(Float) -> Unit> { snapOffset ->
        if (enabled) {
            listState.selectedPage(snapPosition, itemWindow, snapOffset)?.let { page ->
                selection.select(itemKeys[page], items[page])
            }
        }
    }
    return remember(listState) {
        PagerSelectedActionsHandler { snapOffset -> onSnapOffsetCalculated.value(snapOffset) }
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

private fun LazyListState.selectedPage(
    snapPosition: SnapPosition,
    itemWindow: PagerItemWindow,
    snapOffset: Float,
): Int? {
    val info = layoutInfo
    if (itemWindow.realItemCount == 0 || info.totalItemsCount != itemWindow.itemCount) return null
    // Different cyclic windows can have the same virtual item count.
    if (info.visibleItemsInfo.any { it.contentType != itemWindow }) return null
    val page = info.visibleItemsInfo.minByOrNull { abs(info.snapOffset(it, snapPosition) - snapOffset) }
    return page?.index?.let(itemWindow::realIndex)
}
