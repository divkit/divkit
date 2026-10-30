package com.yandex.div.compose.views.tabs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.context.DivViewCoroutineScopeProvider
import com.yandex.div.compose.context.LocalDivViewContext
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.scroll.ScrollTarget
import com.yandex.div.compose.utils.scroll.toScrollOverflow
import com.yandex.div.render.common.scroll.DivScrollActionUtils
import com.yandex.div2.Div
import com.yandex.div2.DivActionScrollBy
import kotlin.math.sign
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Registers the tabs as a scroll action target: actions switch tabs the way a title tap does, over the action's
 * `duration` when it has one. A null [state] registers tabs without items, so actions addressing them validate
 * their arguments without switching anything.
 */
@Composable
internal fun BindTabsScrollActions(
    id: String?,
    items: List<Div>,
    state: DivTabsState? = null,
    animation: TabTitleAnimation? = null,
) {
    if (id == null) return

    val component = LocalDivViewContext.current.component
    val targetStorage = component.scrollTargetStorage
    val coroutineScopeProvider = component.coroutineScopeProvider
    val reporter = LocalComponent.current.reporter
    val currentAnimation by rememberUpdatedState(animation)
    DisposableEffect(id, items, state) {
        val selector = state?.let { TabSelector(it, items, coroutineScopeProvider) { currentAnimation } }
        val target = ScrollTarget(
            items = items,
            scrollTo = { index, animated, _, duration -> selector?.selectTab(index, animated, duration) },
            scrollToOffset = { offset, _, _ -> reporter.reportOffsetIgnored(id, offset) },
            scrollBy = { step, offset, overflow, animated, duration ->
                if (offset != 0) reporter.reportOffsetIgnored(id, offset)
                if (step != 0) selector?.selectTabBy(step, overflow, animated, duration)
            },
        )
        targetStorage.register(id, target)
        onDispose {
            targetStorage.unregister(id, target)
            selector?.cancel()
        }
    }
}

private fun DivReporter.reportOffsetIgnored(id: String, offset: Int) {
    reportWarning("Tabs '$id' switch by tabs, offset $offset is ignored")
}

/** Selects tabs for scroll actions, one action at a time: a new action replaces the running one. */
private class TabSelector(
    private val state: DivTabsState,
    private val items: List<Div>,
    private val coroutineScopeProvider: DivViewCoroutineScopeProvider,
    private val animation: () -> TabTitleAnimation?,
) {
    private var job: Job? = null
    /** The tab of the latest action until it completes; relative actions count from it. */
    private var pending: Request? = null

    fun selectTab(index: Int, animated: Boolean, duration: Int?) {
        val scope = coroutineScopeProvider.get() ?: return
        job?.cancel()
        val request = Request(index)
        pending = request
        job = scope.launch {
            try {
                state.selectTab(index, if (animated) animation() else null, duration)
            } finally {
                if (pending === request) pending = null
            }
        }
    }

    fun selectTabBy(step: Int, overflow: DivActionScrollBy.Overflow, animated: Boolean, duration: Int?) {
        val current = pending?.page ?: state.selectedIndex
        val target = DivScrollActionUtils.targetItem(
            nearestItem = current + step.sign,
            itemCount = items.size,
            step = step,
            overflow = overflow.toScrollOverflow(),
        )
        selectTab(target, animated, duration)
    }

    fun cancel() {
        job?.cancel()
    }

    private class Request(val page: Int)
}
