package com.yandex.div.compose.views.gallery.scroll

import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.yandex.div.compose.context.DivViewCoroutineScopeProvider
import com.yandex.div.compose.context.LocalDivViewContext
import com.yandex.div.compose.observer.observeGalleryScroll
import com.yandex.div.compose.observer.rememberGalleryScrollObservation
import com.yandex.div.compose.scroll.ScrollTarget
import com.yandex.div.compose.utils.scroll.toScrollOverflow
import com.yandex.div.compose.views.gallery.GalleryProperties
import com.yandex.div.compose.views.tabs.LocalIsMeasurementPass
import com.yandex.div.render.common.scroll.DivScrollActionUtils
import com.yandex.div2.Div
import com.yandex.div2.DivActionScrollBy
import com.yandex.div2.DivGallery
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Registers a scrollable gallery as a scroll action target and observes its scrolling.
 */
@Composable
internal fun Modifier.galleryScrolling(
    properties: GalleryProperties,
    items: List<Div>,
    layout: GalleryScrollLayout,
): Modifier {
    val observation = rememberGalleryScrollObservation(properties.id, layout)
    BindGalleryScrollActions(
        id = properties.id,
        items = items,
        layout = layout,
        scrollMode = properties.scrollMode,
        alignment = properties.scrollContentAlignment,
        actionRunner = observation,
    )
    return observeGalleryScroll(observation)
}

/**
 * Registers the gallery as a scroll action target. A null [layout] registers a gallery that cannot scroll,
 * so actions addressing it validate their arguments without moving anything.
 */
@Composable
internal fun BindGalleryScrollActions(
    id: String?,
    items: List<Div>,
    layout: GalleryScrollLayout? = null,
    scrollMode: DivGallery.ScrollMode = DivGallery.ScrollMode.DEFAULT,
    alignment: DivGallery.ContentAlignment = DivGallery.ContentAlignment.START,
    actionRunner: GalleryScrollActionRunner? = null,
) {
    if (id == null || LocalIsMeasurementPass.current) return

    val component = LocalDivViewContext.current.component
    val targetStorage = component.scrollTargetStorage
    val coroutineScopeProvider = component.coroutineScopeProvider
    val density = LocalDensity.current
    val currentScrollMode by rememberUpdatedState(scrollMode)
    val currentAlignment by rememberUpdatedState(alignment)
    DisposableEffect(id, items, layout, density, actionRunner) {
        val scrolls = layout?.let {
            GalleryScrolls(
                it, items, coroutineScopeProvider, density, { currentScrollMode }, { currentAlignment }, actionRunner,
            )
        }
        val target = ScrollTarget(
            items = items,
            scrollTo = { index, animated, toEnd, duration ->
                scrolls?.scrollTo(GalleryScrollDestination(index, toEnd), animated, duration)
            },
            scrollToOffset = { offset, animated, duration ->
                scrolls?.scrollToOffset(offset, animated, duration)
            },
            scrollBy = { step, offset, overflow, animated, duration ->
                scrolls?.scrollBy(step, offset, overflow, animated, duration)
            },
        )
        targetStorage.register(id, target)
        onDispose {
            targetStorage.unregister(id, target)
            scrolls?.cancel()
        }
    }
}

/**
 * Runs scroll actions for [BindGalleryScrollActions]. An action can scroll in several phases or jump
 * instantly, so only its caller knows where it starts and ends.
 */
internal interface GalleryScrollActionRunner {
    suspend fun run(action: suspend () -> Unit)

    /** Forgets the running action: its scroll target is being released. */
    fun discard()
}

/** Runs one scroll action at a time on [layout]; a new action cancels the running one. */
private class GalleryScrolls(
    private val layout: GalleryScrollLayout,
    private val items: List<Div>,
    private val coroutineScopeProvider: DivViewCoroutineScopeProvider,
    private val density: Density,
    private val scrollMode: () -> DivGallery.ScrollMode,
    private val alignment: () -> DivGallery.ContentAlignment,
    private val actionRunner: GalleryScrollActionRunner?,
) {
    private var job: Job? = null

    fun scrollTo(destination: GalleryScrollDestination, animated: Boolean, duration: Int?) = launch {
        layout.scrollToItem(destination, animated, alignment(), duration)
    }

    fun scrollToOffset(offset: Int, animated: Boolean, duration: Int?) {
        val state = offsetScrollState() ?: return
        launch { state.scrollByDistance((offset.dpToPx() - state.scrollOffset).toFloat(), animated, duration) }
    }

    fun scrollBy(step: Int, offset: Int, overflow: DivActionScrollBy.Overflow, animated: Boolean, duration: Int?) {
        if (step != 0 && items.isNotEmpty()) {
            launch {
                val index = DivScrollActionUtils.targetItem(
                    layout.layoutInfo.nearestItem(step, alignment()), items.size, step, overflow.toScrollOverflow(),
                )
                val offsetPx = if (scrollMode() == DivGallery.ScrollMode.DEFAULT) offset.dpToPx() else 0
                val destination = GalleryScrollDestination(index, offset = offsetPx, overflow = overflow)
                layout.scrollToItem(destination, animated, alignment(), duration)
            }
        } else if (offset != 0) {
            val state = offsetScrollState() ?: return
            launch {
                state.scrollByDistance(state.galleryOffsetDistance(offset.dpToPx(), overflow), animated, duration)
            }
        }
    }

    fun cancel() {
        actionRunner?.discard()
        job?.cancel()
    }

    private fun launch(action: suspend () -> Unit) {
        val scope = coroutineScopeProvider.get() ?: return
        job?.cancel()
        job = scope.launch {
            if (actionRunner == null) action() else actionRunner.run(action)
        }
    }

    // Offsets address a scroll position, which paging galleries do not keep.
    private fun offsetScrollState() = layout.scrollState.takeIf { scrollMode() == DivGallery.ScrollMode.DEFAULT }

    private fun Int.dpToPx(): Int = with(density) { dp.roundToPx() }
}

private val ScrollableState.scrollOffset: Int
    get() = requireNotNull(scrollIndicatorState).scrollOffset

/** Distance of a scroll by [offset] from the position that is [itemDistance] away from the current one. */
internal fun ScrollableState.galleryOffsetDistance(
    offset: Int,
    overflow: DivActionScrollBy.Overflow,
    itemDistance: Float = 0f,
): Float {
    val state = requireNotNull(scrollIndicatorState)
    val range = state.contentSize - state.viewportSize
    val from = state.scrollOffset + itemDistance.toInt()
    val target = DivScrollActionUtils.targetOffset(from, range, offset, overflow.toScrollOverflow())
    return (target - state.scrollOffset).toFloat()
}

private suspend fun ScrollableState.scrollByDistance(distance: Float, animated: Boolean, duration: Int?) {
    when {
        !animated -> scrollBy(distance)
        duration == null -> animateScrollBy(distance)
        else -> animateScrollBy(distance, tween(duration))
    }
}
