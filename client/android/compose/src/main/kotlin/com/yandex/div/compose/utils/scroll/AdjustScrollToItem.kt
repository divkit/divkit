package com.yandex.div.compose.utils.scroll

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.first

@Composable
internal fun AdjustScrollToItem(
    listState: LazyListState,
    targetIndex: Int,
    desiredOffset: (viewportSize: Int, itemSize: Int) -> Int,
) {
    LaunchedEffect(Unit) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .first { items -> items.any { it.index == targetIndex } }

        val layoutInfo = listState.layoutInfo
        val targetItem = layoutInfo.visibleItemsInfo.firstOrNull { it.index == targetIndex }
            ?: return@LaunchedEffect

        val viewportSize = layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset
        val offset = desiredOffset(viewportSize, targetItem.size)

        listState.scroll {
            scrollBy((targetItem.offset - offset).toFloat())
        }
    }
}
