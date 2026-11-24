package com.yandex.div.compose.observer

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Velocity
import com.yandex.div.compose.context.LocalDivViewContext
import com.yandex.div.compose.observer.events.DivTabChangedEvent
import com.yandex.div.compose.observer.events.DivTabTitlesScrolledEvent

/**
 * Returns the observation of the selected tab, or null when it is not observed.
 */
@Composable
internal fun rememberTabSelectionObservation(tabsId: String?): TabSelectionObservation? {
    val observer = activeComponentObserver ?: return null
    val component = LocalDivViewContext.current.component
    return remember(component, tabsId) { TabSelectionObservation(observer, component.data.logId, tabsId) }
}

internal class TabSelectionObservation(
    private val observer: DivComponentObserver,
    private val cardId: String,
    private val tabsId: String?,
) {
    private var previousIndex: Int? = null

    /**
     * Reports changes of the settled tab. Call it after selected_tab has been applied:
     * the tab settled at the first call is the baseline of a new card.
     */
    suspend fun observe(pagerState: PagerState) {
        snapshotFlow { pagerState.settledPage }.collect(::onSettled)
    }

    private fun onSettled(index: Int) {
        val previous = previousIndex
        previousIndex = index
        if (previous != null && previous != index) {
            observer.onTabChanged(DivTabChangedEvent(cardId, tabsId, index))
        }
    }
}

@Composable
internal fun Modifier.observeTitleScroll(
    listState: LazyListState,
    pagerState: PagerState,
    tabsId: String?,
): Modifier {
    val observer = activeComponentObserver ?: return this
    val component = LocalDivViewContext.current.component
    val connection = remember(component, tabsId, pagerState) {
        TitleScrollConnection {
            observer.onTabTitlesScrolled(
                DivTabTitlesScrolledEvent(component.data.logId, tabsId, pagerState.settledPage)
            )
        }
    }
    // Wheel and keyboard scrolling can finish without a fling.
    LaunchedEffect(listState, connection) {
        snapshotFlow { connection.reported && !listState.isScrollInProgress }.collect { finished ->
            if (finished) connection.reported = false
        }
    }
    return nestedScroll(connection)
}

private class TitleScrollConnection(private val onScrolled: () -> Unit) : NestedScrollConnection {
    var reported by mutableStateOf(false)

    override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
        if (!reported && source == NestedScrollSource.UserInput && consumed.x != 0f) {
            reported = true
            onScrolled()
        }
        return Offset.Zero
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        reported = false
        return Velocity.Zero
    }
}
