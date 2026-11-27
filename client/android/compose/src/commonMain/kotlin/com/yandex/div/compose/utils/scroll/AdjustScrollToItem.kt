package com.yandex.div.compose.utils.scroll

import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.lazy.LazyListState
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
    onAdjusted: () -> Unit = {},
    desiredOffset: (viewportSize: Int, itemSize: Int) -> Int,
) {
    val initialTargetIndex = remember(listState) { targetIndex }
    var lastAlignedPosition by rememberSaveable(listState, stateSaver = AlignedScrollPosition.Saver) {
        mutableStateOf<AlignedScrollPosition?>(null)
    }
    val currentDesiredOffset by rememberUpdatedState(desiredOffset)
    val currentOnAdjusted by rememberUpdatedState(onAdjusted)
    LaunchedEffect(listState, targetIndex, restartKey) {
        val previousPosition = lastAlignedPosition
        if (listState.isScrollInProgress ||
            previousPosition != null && previousPosition != listState.scrollPosition
        ) {
            currentOnAdjusted()
            return@LaunchedEffect
        }

        if (targetIndex != initialTargetIndex) {
            listState.scrollToItem(targetIndex)
        }
        val item = snapshotFlow { listState.layoutInfo.visibleItemsInfo.firstOrNull { it.index == targetIndex } }
            .first { it != null } ?: return@LaunchedEffect
        val viewportSize = listState.layoutInfo.run { viewportEndOffset - viewportStartOffset }
        listState.scrollBy((item.offset - currentDesiredOffset(viewportSize, item.size)).toFloat())
        lastAlignedPosition = listState.scrollPosition
        currentOnAdjusted()
    }
}

private val LazyListState.scrollPosition: AlignedScrollPosition
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
