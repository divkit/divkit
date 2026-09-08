package com.yandex.div.compose.views.tabs

import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlin.math.abs

@Composable
internal fun rememberDivTabsState(
    initialIndex: Int,
    tabCount: Int,
): DivTabsState {
    val state = remember { DivTabsState(initialIndex = initialIndex, initialTabCount = tabCount) }
    // The state outlives item updates: the pager clamps its current page to the new count,
    // as the View renderer keeps min(page, size - 1) when it rebinds new items.
    state.tabCount = tabCount
    return state
}

internal class DivTabsState(
    initialIndex: Int = 0,
    initialOffsetFraction: Float = 0f,
    initialTabCount: Int = 0,
) {
    internal var tabCount: Int by mutableIntStateOf(initialTabCount)

    internal val pagerState: PagerState = PagerState(
        currentPage = initialIndex.coerceAtLeast(0),
        currentPageOffsetFraction = initialOffsetFraction,
        pageCount = { tabCount },
    )

    internal val currentIndex: Int
        get() = pagerState.currentPage

    internal suspend fun selectTab(index: Int, animated: Boolean = true) {
        if (tabCount <= 0) return
        val target = index.coerceIn(0, tabCount - 1)
        if (animated) {
            pagerState.animateScrollToPage(target)
        } else {
            pagerState.scrollToPage(target)
        }
    }
}

internal val PagerState.logicalPosition: Float
    get() {
        val offset = currentPageOffsetFraction.let { if (abs(it) < PAGE_OFFSET_EPSILON) 0f else it }
        return (currentPage.toFloat() + offset)
            .coerceIn(0f, (pageCount - 1).coerceAtLeast(0).toFloat())
    }

private const val PAGE_OFFSET_EPSILON = 0.00001f
