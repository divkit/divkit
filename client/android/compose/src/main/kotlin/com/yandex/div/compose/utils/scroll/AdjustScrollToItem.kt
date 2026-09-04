package com.yandex.div.compose.utils.scroll

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.first

@Composable
internal fun AdjustScrollToItem(
    listState: LazyListState,
    targetIndex: Int,
    restartKey: Any? = Unit,
    desiredOffset: (viewportSize: Int, itemSize: Int) -> Int,
) {
    val initialTargetIndex = remember(listState) { targetIndex }
    var lastAlignedPosition by rememberSaveable(listState, stateSaver = AlignedScrollPosition.Saver) {
        mutableStateOf<AlignedScrollPosition?>(null)
    }
    val currentDesiredOffset by rememberUpdatedState(desiredOffset)
    LaunchedEffect(listState, targetIndex, restartKey) {
        val previousPosition = lastAlignedPosition
        if (listState.isScrollInProgress ||
            previousPosition != null && previousPosition != listState.scrollPosition
        ) return@LaunchedEffect

        if (targetIndex != initialTargetIndex) {
            listState.scrollToItem(targetIndex)
        }
        listState.adjustScrollToItem(
            item = {
                listState.layoutInfo.visibleItemsInfo.firstOrNull { it.index == targetIndex }
                    ?.let { MeasuredScrollItem(it.offset, it.size) }
            },
            viewportSize = {
                listState.layoutInfo.run { viewportEndOffset - viewportStartOffset }
            },
            desiredOffset = currentDesiredOffset,
        )
        lastAlignedPosition = listState.scrollPosition
    }
}

@Composable
internal fun AdjustScrollToItem(
    gridState: LazyGridState,
    targetIndex: Int,
    isHorizontal: Boolean,
    restartKey: Any? = Unit,
    desiredOffset: (viewportSize: Int, itemSize: Int) -> Int,
) {
    val initialTargetIndex = remember(gridState) { targetIndex }
    var lastAlignedPosition by rememberSaveable(gridState, stateSaver = AlignedScrollPosition.Saver) {
        mutableStateOf<AlignedScrollPosition?>(null)
    }
    val currentDesiredOffset by rememberUpdatedState(desiredOffset)
    LaunchedEffect(gridState, targetIndex, isHorizontal, restartKey) {
        val previousPosition = lastAlignedPosition
        if (gridState.isScrollInProgress ||
            previousPosition != null && previousPosition != gridState.scrollPosition
        ) return@LaunchedEffect

        if (targetIndex != initialTargetIndex) {
            gridState.scrollToItem(targetIndex)
        }
        gridState.adjustScrollToItem(
            item = {
                gridState.layoutInfo.visibleItemsInfo.firstOrNull { it.index == targetIndex }
                    ?.let {
                        if (isHorizontal) {
                            MeasuredScrollItem(it.offset.x, it.size.width)
                        } else {
                            MeasuredScrollItem(it.offset.y, it.size.height)
                        }
                    }
            },
            viewportSize = {
                gridState.layoutInfo.run { viewportEndOffset - viewportStartOffset }
            },
            desiredOffset = currentDesiredOffset,
        )
        lastAlignedPosition = gridState.scrollPosition
    }
}

private suspend fun ScrollableState.adjustScrollToItem(
    item: () -> MeasuredScrollItem?,
    viewportSize: () -> Int,
    desiredOffset: (viewportSize: Int, itemSize: Int) -> Int,
) {
    val targetItem = snapshotFlow { item() }.first { it != null } ?: return
    val offset = desiredOffset(viewportSize(), targetItem.size)

    scroll {
        scrollBy((targetItem.offset - offset).toFloat())
    }
}

private data class MeasuredScrollItem(
    val offset: Int,
    val size: Int,
)

private val LazyListState.scrollPosition: AlignedScrollPosition
    get() = AlignedScrollPosition(firstVisibleItemIndex, firstVisibleItemScrollOffset)

private val LazyGridState.scrollPosition: AlignedScrollPosition
    get() = AlignedScrollPosition(firstVisibleItemIndex, firstVisibleItemScrollOffset)

private data class AlignedScrollPosition(
    val itemIndex: Int,
    val scrollOffset: Int,
) {
    companion object {
        val Saver = listSaver<AlignedScrollPosition?, Int>(
            save = { position ->
                position?.let { listOf(it.itemIndex, it.scrollOffset) } ?: emptyList()
            },
            restore = { AlignedScrollPosition(it[0], it[1]) },
        )
    }
}
