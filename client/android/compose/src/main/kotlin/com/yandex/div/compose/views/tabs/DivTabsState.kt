package com.yandex.div.compose.views.tabs

import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
internal fun rememberDivTabsState(
    initialIndex: Int,
    initialTabCount: Int,
): DivTabsState = remember {
    DivTabsState(initialIndex = initialIndex, initialTabCount = initialTabCount)
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
