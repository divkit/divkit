package com.yandex.div.compose.views.pager

import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.lazy.LazyLayoutScrollScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.context.LocalDivViewContext
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.pager.mainAxisViewportSize
import com.yandex.div.compose.pager.snapOffset
import com.yandex.div.compose.pager.snappedItem
import com.yandex.div.compose.scroll.ScrollTarget
import com.yandex.div.compose.utils.scroll.toScrollOverflow
import com.yandex.div.render.common.pager.PagerItemWindow
import com.yandex.div.render.common.scroll.DivScrollActionUtils
import com.yandex.div2.Div
import com.yandex.div2.DivActionScrollBy
import kotlin.math.abs
import kotlin.math.sign
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Registers the pager as a scroll action target. A null [layout] registers a pager without pages, so actions
 * addressing it validate their arguments without moving anything.
 */
@Composable
internal fun BindPagerScrollActions(
    id: String?,
    items: List<Div>,
    layout: PagerScrollLayout? = null,
) {
    if (id == null) return

    val targetStorage = LocalDivViewContext.current.component.scrollTargetStorage
    val reporter = LocalComponent.current.reporter
    val scope = rememberCoroutineScope()
    DisposableEffect(targetStorage, id, items, layout) {
        val scrolls = layout?.let { PagerScrolls(it, items, scope) }
        val target = ScrollTarget(
            items = items,
            scrollTo = { index, animated, _, duration -> scrolls?.scrollTo(index, animated, duration) },
            scrollToOffset = { offset, _, _ -> reporter.reportOffsetIgnored(id, offset) },
            scrollBy = { step, offset, overflow, animated, duration ->
                if (offset != 0) reporter.reportOffsetIgnored(id, offset)
                if (step != 0) scrolls?.scrollBy(step, overflow, animated, duration)
            },
        )
        targetStorage.register(id, target)
        onDispose {
            targetStorage.unregister(id, target)
            scrolls?.cancel()
        }
    }
}

// The View renderer's pager has no scroll offset either: DivViewWithItems.Pager leaves scrollTo unimplemented.
private fun DivReporter.reportOffsetIgnored(id: String, offset: Int) {
    reportWarning("Pager '$id' scrolls by pages, offset $offset is ignored")
}

/** Pager layout driven by scroll actions; raw indices address the pages of its [itemWindow]. */
internal class PagerScrollLayout(
    private val listState: LazyListState,
    val itemWindow: PagerItemWindow,
    /** Raw index of the page the pager opens on. */
    private val initialPage: Int,
    private val snapPosition: SnapPosition,
    /** Main axis size of a page that is not laid out, when known. */
    private val pageSize: (Int) -> Int?,
    private val selectedActionsHandler: PagerSelectedActionsHandler,
) {
    /**
     * Raw index of the page closest to its snap position. Before the first layout it is the initial page: the
     * first visible item is not the page itself when the page is centred or end-aligned.
     */
    val currentPage: Int
        get() = listState.layoutInfo.snappedItem(snapPosition)?.index ?: initialPage

    /**
     * Moves to [page]: instantly, over an explicit [duration], or with the lazy list's own smooth scroll. The page
     * is selected when the scroll starts, except for a timed scroll, which selects only the page it reaches.
     */
    suspend fun scrollToPage(page: Int, animated: Boolean, duration: Int?) {
        if (!animated || duration == null) selectedActionsHandler.onTargetPageCalculated(page)
        val scrollOffset = snapScrollOffset(page)
        when {
            !animated -> listState.scrollToItem(page, scrollOffset)
            duration == null -> listState.animateScrollToItem(page, scrollOffset)
            else -> {
                var distance = 0
                listState.scroll {
                    distance = LazyLayoutScrollScope(listState, this).calculateDistanceTo(page, scrollOffset)
                }
                listState.animateScrollBy(distance.toFloat(), tween(duration))
            }
        }
        alignPage(page)
    }

    /** The scroll offset that places [page] at its snap position; a page not laid out yet is sized by estimate. */
    private fun snapScrollOffset(page: Int): Int {
        val info = listState.layoutInfo
        val size = info.visibleItemsInfo.firstOrNull { it.index == page }?.size
            ?: pageSize(page)
            ?: info.snappedItem(snapPosition)?.size
            ?: 0
        return -snapPosition.position(
            layoutSize = info.mainAxisViewportSize,
            itemSize = size,
            beforeContentPadding = info.beforeContentPadding,
            afterContentPadding = info.afterContentPadding,
            itemIndex = page,
            itemCount = info.totalItemsCount,
        )
    }

    /** A page sized by estimate before the scroll lands at its measured size afterwards. */
    private suspend fun alignPage(page: Int) {
        val info = listState.layoutInfo
        val item = info.visibleItemsInfo.firstOrNull { it.index == page } ?: return
        val offset = info.snapOffset(item, snapPosition)
        if (abs(offset) >= 1f) listState.scrollBy(offset)
    }
}

/** Runs one scroll action at a time on [layout]; a new action replaces the running one, as the View renderer does. */
private class PagerScrolls(
    private val layout: PagerScrollLayout,
    private val items: List<Div>,
    private val scope: CoroutineScope,
) {
    private var job: Job? = null
    /** The page of the latest action until it completes; relative actions count from it. */
    private var pending: Request? = null

    fun scrollTo(index: Int, animated: Boolean, duration: Int?) {
        launch(nearestCopy(index), animated, duration)
    }

    fun scrollBy(step: Int, overflow: DivActionScrollBy.Overflow, animated: Boolean, duration: Int?) {
        val current = currentPage()
        val window = layout.itemWindow
        val page = if (window.edgeItemCount == 0) {
            DivScrollActionUtils.targetItem(
                nearestItem = current + step.sign,
                itemCount = items.size,
                step = step,
                overflow = overflow.toScrollOverflow(),
            )
        } else {
            // A cyclic window wraps on its own; overflow never applies.
            (current + step).coerceIn(0, window.itemCount - 1)
        }
        launch(page, animated, duration)
    }

    fun cancel() {
        job?.cancel()
    }

    private fun currentPage(): Int = pending?.page ?: layout.currentPage

    /** The copy of real [index] closest to the current page in a cyclic window, the index itself otherwise. */
    private fun nearestCopy(index: Int): Int {
        val window = layout.itemWindow
        if (window.edgeItemCount == 0) return index
        val current = currentPage()
        var delta = (index - window.realIndex(current)).mod(window.realItemCount)
        if (delta > window.realItemCount / 2) delta -= window.realItemCount
        return (current + delta).coerceIn(0, window.itemCount - 1)
    }

    private fun launch(page: Int, animated: Boolean, duration: Int?) {
        job?.cancel()
        val request = Request(page)
        pending = request
        job = scope.launch {
            try {
                layout.scrollToPage(page, animated, duration)
            } finally {
                if (pending === request) pending = null
            }
        }
    }

    private class Request(val page: Int)
}
