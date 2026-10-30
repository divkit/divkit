package com.yandex.div.compose.views.gallery.scroll

import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.yandex.div.compose.context.DivViewCoroutineScopeProvider
import com.yandex.div.compose.context.LocalDivViewContext
import com.yandex.div.compose.scroll.ScrollTarget
import com.yandex.div.compose.utils.scroll.toScrollOverflow
import com.yandex.div.render.common.scroll.DivScrollActionUtils
import com.yandex.div2.Div
import com.yandex.div2.DivActionScrollBy
import com.yandex.div2.DivGallery
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

// DivGallerySmoothScroller pace: 50 ms per inch of seeking, extended by the deceleration phase.
private const val MILLISECONDS_PER_INCH = 50f
private const val LINEAR_SCROLL_FRACTION = .3356f
private const val DP_PER_INCH = 160f
private const val MAX_DEFAULT_SCROLL_DURATION = 2_000

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
) {
    if (id == null) return

    val component = LocalDivViewContext.current.component
    val targetStorage = component.scrollTargetStorage
    val coroutineScopeProvider = component.coroutineScopeProvider
    val density = LocalDensity.current
    val currentScrollMode by rememberUpdatedState(scrollMode)
    val currentAlignment by rememberUpdatedState(alignment)
    DisposableEffect(id, items, layout, density) {
        val scrolls = layout?.let {
            GalleryScrolls(it, items, coroutineScopeProvider, density, { currentScrollMode }, { currentAlignment })
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

/** Runs one scroll action at a time on [layout]; a new action cancels the running one, as the View renderer does. */
private class GalleryScrolls(
    private val layout: GalleryScrollLayout,
    private val items: List<Div>,
    private val coroutineScopeProvider: DivViewCoroutineScopeProvider,
    private val density: Density,
    private val scrollMode: () -> DivGallery.ScrollMode,
    private val alignment: () -> DivGallery.ContentAlignment,
) {
    private var job: Job? = null

    fun scrollTo(destination: GalleryScrollDestination, animated: Boolean, duration: Int?) = launch {
        layout.scrollToItem(destination, animated, alignment(), duration)
    }

    fun scrollToOffset(offset: Int, animated: Boolean, duration: Int?) {
        val state = offsetScrollState() ?: return
        launch { state.scrollToGalleryOffset(offset.dpToPx(), animated, duration, density) }
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
            launch { state.scrollByGalleryOffset(offset.dpToPx(), overflow, animated, duration, density) }
        }
    }

    fun cancel() {
        job?.cancel()
    }

    private fun launch(action: suspend () -> Unit) {
        val scope = coroutineScopeProvider.get() ?: return
        job?.cancel()
        job = scope.launch { action() }
    }

    // Offsets address a scroll position, which paging galleries do not keep.
    private fun offsetScrollState() = layout.scrollState.takeIf { scrollMode() == DivGallery.ScrollMode.DEFAULT }

    private fun Int.dpToPx(): Int = with(density) { dp.roundToPx() }
}

internal suspend fun ScrollableState.scrollToGalleryOffset(
    offset: Int,
    animated: Boolean,
    duration: Int?,
    density: Density,
) {
    val state = requireNotNull(scrollIndicatorState)
    scrollByDistance((offset - state.scrollOffset).toFloat(), animated, duration, density)
}

internal suspend fun ScrollableState.scrollByGalleryOffset(
    offset: Int,
    overflow: DivActionScrollBy.Overflow,
    animated: Boolean,
    duration: Int?,
    density: Density,
) {
    scrollByDistance(galleryOffsetDistance(offset, overflow), animated, duration, density)
}

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

internal suspend fun ScrollableState.scrollByDistance(
    distance: Float,
    animated: Boolean,
    duration: Int?,
    density: Density,
): Float = if (animated) {
    animateScrollBy(distance, tween(durationMillis = duration ?: defaultScrollDuration(distance, density)))
} else {
    scrollBy(distance)
}

/** Duration of an animation without an explicit one: the View renderer's smooth scroller pace. */
internal fun defaultScrollDuration(distancePx: Float, density: Density): Int {
    val inches = abs(distancePx) / (density.density * DP_PER_INCH)
    return (inches * MILLISECONDS_PER_INCH / LINEAR_SCROLL_FRACTION).roundToInt()
        .coerceIn(1, MAX_DEFAULT_SCROLL_DURATION)
}

