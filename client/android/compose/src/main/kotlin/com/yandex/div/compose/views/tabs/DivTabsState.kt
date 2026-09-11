package com.yandex.div.compose.views.tabs

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.yandex.div2.DivTabs
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
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

    private var titleTransition: TitleTransition? by mutableStateOf(null)

    internal val currentIndex: Int
        get() = pagerState.currentPage

    internal val selectedIndex: Int
        get() = titleTransition?.to ?: pagerState.currentPage

    internal val indicatorFromIndex: Int
        get() = titleTransition?.from ?: pagerState.currentPage

    internal val indicatorToIndex: Int
        get() {
            val transition = titleTransition
            if (transition != null) return transition.to
            val fraction = pagerState.currentPageOffsetFraction
            return when {
                fraction > 0f -> pagerState.currentPage + 1
                fraction < 0f -> pagerState.currentPage - 1
                else -> pagerState.currentPage
            }
        }

    internal val indicatorProgress: Float
        get() = titleTransition?.progress?.value ?: abs(pagerState.currentPageOffsetFraction)

    internal suspend fun selectTab(index: Int, animation: TabTitleAnimation?) {
        if (tabCount <= 0) return
        val target = index.coerceIn(0, tabCount - 1)
        if (animation == null) {
            pagerState.scrollToPage(target)
            return
        }
        val from = selectedIndex
        if (from == target) {
            if (titleTransition == null) {
                pagerState.animateScrollToPage(target)
            }
            return
        }

        val transition = TitleTransition(from = from, to = target)
        titleTransition = transition
        try {
            coroutineScope {
                launch { transition.run(animation) }
                pagerState.animateScrollToPage(target)
            }
        } finally {
            if (titleTransition === transition) {
                titleTransition = null
            }
        }
    }
}

internal data class TabTitleAnimation(
    val type: DivTabs.TabTitleStyle.AnimationType,
    val durationMillis: Int,
)

private class TitleTransition(val from: Int, val to: Int) {
    val progress = Animatable(0f)

    suspend fun run(animation: TabTitleAnimation) {
        when (animation.type) {
            DivTabs.TabTitleStyle.AnimationType.SLIDE,
            DivTabs.TabTitleStyle.AnimationType.FADE -> progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(animation.durationMillis, easing = FastOutSlowInEasing),
            )
            DivTabs.TabTitleStyle.AnimationType.NONE -> progress.snapTo(1f)
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
