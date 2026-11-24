package com.yandex.div.compose.observer

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Velocity
import com.yandex.div.compose.context.LocalDivViewContext
import com.yandex.div.compose.observer.events.DivGalleryScrollEvent
import com.yandex.div.compose.observer.events.DivScrollDirection
import com.yandex.div.compose.views.gallery.scroll.GalleryLayoutInfo
import com.yandex.div.compose.views.gallery.scroll.GalleryLayoutItem
import com.yandex.div.compose.views.gallery.scroll.GalleryScrollActionRunner
import com.yandex.div.compose.views.gallery.scroll.GalleryScrollLayout
import kotlin.math.abs
import kotlin.math.sign
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val SIGNIFICANT_SCROLL_DIVISOR = 20
// A wheel session in a descendant has neither an ancestor scroll-state transition nor a fling callback.
private const val NESTED_WHEEL_IDLE_MS = 80L

/**
 * Returns the observation of a scrollable gallery, or null when its scrolling is not observed.
 */
@Composable
internal fun rememberGalleryScrollObservation(
    galleryId: String?,
    layout: GalleryScrollLayout,
): GalleryScrollObservation? {
    val observer = activeComponentObserver ?: return null
    val component = LocalDivViewContext.current.component
    val observation = remember(component, galleryId, layout) {
        GalleryScrollObservation(layout, observer, component.data.logId, galleryId)
    }
    LaunchedEffect(observation) { observation.observeLayout() }
    return observation
}

/** Lets [observation] see the gestures of the gallery this modifier is applied to. */
internal fun Modifier.observeGalleryScroll(observation: GalleryScrollObservation?): Modifier {
    if (observation == null) return this
    return nestedScroll(observation).pointerInput(observation) {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent(PointerEventPass.Initial)
                observation.updatePointerDown(event.changes.any { it.pressed })
            }
        }
    }
}

/**
 * Tracks one action or gesture (including its fling) across the underlying layout's scroll phases.
 */
internal class GalleryScrollObservation(
    private val layout: GalleryScrollLayout,
    private val observer: DivComponentObserver,
    private val cardId: String,
    private val galleryId: String?,
) : NestedScrollConnection, GalleryScrollActionRunner {
    private val measuredState = layout.scrollState as? ScrollState
    private var beforeInput: Position? = null
    private var movement by mutableStateOf<Movement?>(null)
    private var flinging by mutableStateOf(false)
    private var pointerDown by mutableStateOf(false)

    fun updatePointerDown(pressed: Boolean) {
        if (pressed && !pointerDown) {
            if (movement?.isAction == false) finish()
            flinging = false
        }
        pointerDown = pressed
    }

    suspend fun observeLayout() {
        snapshotFlow { idleMovement?.let { it to it.wheelTick } }.collectLatest { pending ->
            val (idle, tick) = pending ?: return@collectLatest
            if (idle.isNested) delay(NESTED_WHEEL_IDLE_MS)
            if (idleMovement === idle && idle.wheelTick == tick) finish()
        }
    }

    private val idleMovement: Movement?
        get() {
            if (pointerDown || flinging || layout.scrollState.isScrollInProgress) return null
            return movement?.takeUnless { it.isAction }
        }

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        beforeInput = if ((source == NestedScrollSource.UserInput || flinging) && movement?.significant != true) {
            Position()
        } else {
            null
        }
        return Offset.Zero
    }

    override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
        val before = beforeInput
        beforeInput = null
        if (before != null) {
            // NestedScroll's consumed includes the child. Only this gallery's own displacement counts.
            val after = Position()
            val delta = after.distanceFrom(before)
            if (delta != 0L) {
                val current = movement?.takeUnless { it.isAction } ?: begin(isAction = false).also {
                    it.isNested = !layout.scrollState.isScrollInProgress
                }
                record(current, delta, after)
            }
        }
        movement?.let { if (it.isNested) it.wheelTick++ }
        return Offset.Zero
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        flinging = true
        return Velocity.Zero
    }

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        flinging = false
        if (movement?.isAction == false) finish()
        return Velocity.Zero
    }

    override suspend fun run(action: suspend () -> Unit): Unit = coroutineScope {
        flinging = false
        val started = begin(isAction = true)
        var previous = Position()
        fun sample(position: Position) {
            record(started, position.distanceFrom(previous), position)
            previous = position
        }
        val samples = launch(start = CoroutineStart.UNDISPATCHED) {
            snapshotFlow { if (started.significant || movement !== started) null else Position() }
                .collect { if (it != null) sample(it) }
        }
        try {
            action()
        } finally {
            samples.cancel()
            if (movement === started) {
                // An instant jump can finish before snapshotFlow samples it.
                if (!started.significant) sample(Position())
                finish()
            }
        }
    }

    override fun discard() {
        if (movement?.isAction == true) movement = null
    }

    private fun begin(isAction: Boolean): Movement {
        finish()
        return Movement(isAction).also { movement = it }
    }

    private fun record(current: Movement, delta: Long, position: Position) {
        if (delta == 0L || current.significant) return
        current.distance += abs(delta)
        current.direction = if (delta > 0) DivScrollDirection.FORWARD else DivScrollDirection.BACKWARD
        if (current.distance > position.viewportSize / SIGNIFICANT_SCROLL_DIVISOR) {
            current.significant = true
            observer.onGallerySignificantScroll(position.layoutInfo().toEvent(cardId, galleryId, current.direction))
        }
    }

    private fun finish() {
        val current = movement ?: return
        movement = null
        if (current.distance > 0) {
            observer.onGalleryScrollCompleted(layout.layoutInfo.toEvent(cardId, galleryId, current.direction))
        }
    }

    private inner class Position {
        // Measured lanes have an exact pixel offset; mapping all their children on every delta is unnecessary.
        private val offset = measuredState?.value ?: 0
        private val info = if (measuredState == null) layout.layoutInfo else null
        val viewportSize = info?.viewportSize ?: requireNotNull(measuredState).viewportSize

        fun layoutInfo(): GalleryLayoutInfo = info ?: layout.layoutInfo

        fun distanceFrom(previous: Position): Long = if (info == null) {
            offset.toLong() - previous.offset
        } else {
            info.scrollDistanceFrom(requireNotNull(previous.info)).toLong()
        }
    }

    private class Movement(val isAction: Boolean) {
        var distance = 0L
        var isNested = false
        var wheelTick by mutableIntStateOf(0)
        var significant = false
        var direction = DivScrollDirection.FORWARD
    }
}

private fun GalleryLayoutInfo.scrollDistanceFrom(previous: GalleryLayoutInfo): Int {
    val common = items.firstNotNullOfOrNull { item -> previous.item(item.index)?.let { it.offset - item.offset } }
    if (common != null) return common
    // No item is laid out in both layouts: after a distant index jump, or while a grid shows only item spacing.
    val indexDelta = firstItemIndex - previous.firstItemIndex
    if (indexDelta != 0) return indexDelta.sign * viewportSize
    return firstItemScrollOffset - previous.firstItemScrollOffset
}

private fun GalleryLayoutInfo.toEvent(
    cardId: String,
    galleryId: String?,
    direction: DivScrollDirection,
): DivGalleryScrollEvent {
    fun visible(item: GalleryLayoutItem) =
        item.size > 0 && item.offset + item.size > viewportStart && item.offset < viewportEnd
    return DivGalleryScrollEvent(
        cardId = cardId,
        galleryId = galleryId,
        firstVisibleItem = items.firstOrNull(::visible)?.index ?: -1,
        lastVisibleItem = items.lastOrNull(::visible)?.index ?: -1,
        scrollDirection = direction,
    )
}
