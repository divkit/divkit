package com.yandex.div.compose.views.slider

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.util.fastFirstOrNull
import com.yandex.div.compose.actions.DivActionSource
import com.yandex.div.compose.dagger.DivLocalComponent
import com.yandex.div.compose.dagger.handleActions
import com.yandex.div2.DivAction
import kotlinx.coroutines.CoroutineScope
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToLong

internal fun Modifier.sliderPointerInput(handler: SliderGestureHandler, isEnabled: Boolean): Modifier {
    if (!isEnabled) return this
    return pointerInput(handler) {
        awaitEachGesture { with(handler) { handleGesture() } }
    }
}

/**
 * Handles one slider gesture the way `SliderView.onTouchEvent` does: the thumb closest to the down event
 * follows the pointer, press actions run on the down and when the gesture ends — by release, by cancellation
 * or by a scrolling parent taking the gesture over.
 *
 * Pointer changes stay unconsumed until the drag exceeds the touch slop, so a scrolling parent can still start
 * its own drag: a mostly vertical movement is left to the parents, as `requestDisallowInterceptTouchEvent(false)`
 * leaves it to a `RecyclerView`, and the thumb follows the pointer until a parent takes the gesture over; a mostly
 * horizontal movement is consumed and keeps the slider in control. The direction is decided again every time the
 * pointer travels the touch slop from the previous decision, as `SliderView` decides it on every `ACTION_MOVE`, so a
 * drag that turns steep later is still handed over to the parent.
 */
internal class SliderGestureHandler(
    private val state: SliderState,
    private val thumbWidth: Float,
    private val hasTickMarks: Boolean,
    private val isRtl: Boolean,
    private val animationsEnabled: Boolean,
    private val coroutineScope: CoroutineScope,
    private val pressHandler: SliderPressHandler,
) {
    suspend fun AwaitPointerEventScope.handleGesture() {
        val down = awaitFirstDown()
        down.consume()
        pressHandler.onPressStart()

        val trackLength = size.width - thumbWidth
        val thumb = closestThumb(down.position.x, trackLength)
        state.isInteracting = true
        try {
            thumb?.setValue(valueAt(down.position.x, trackLength), animated = animationsEnabled, scope = coroutineScope)
            followDrag(down, thumb, trackLength)
        } finally {
            state.isInteracting = false
            pressHandler.onPressEnd()
        }
    }

    private suspend fun AwaitPointerEventScope.followDrag(
        down: PointerInputChange,
        thumb: SliderThumb?,
        trackLength: Float,
    ) {
        var isSliderDrag = false
        var decisionPosition = down.position
        while (true) {
            val change = awaitPointerEvent().changes.fastFirstOrNull { it.id == down.id } ?: return
            if (!change.pressed) {
                change.consume()
                return
            }
            val drag = change.position - decisionPosition
            if (max(abs(drag.x), abs(drag.y)) >= viewConfiguration.touchSlop) {
                isSliderDrag = abs(drag.x) >= abs(drag.y)
                decisionPosition = change.position
            }
            thumb?.setValue(valueAt(change.position.x, trackLength), animated = false, scope = coroutineScope)
            if (isSliderDrag) {
                change.consume()
            } else {
                // A scrolling parent that takes the gesture consumes the change in its Main pass, after the slider.
                awaitPointerEvent(PointerEventPass.Final)
                if (change.isConsumed) return
            }
        }
    }

    private fun closestThumb(x: Float, trackLength: Float): SliderThumb? {
        if (trackLength <= 0f) return null
        return state.closestThumb(position = x - thumbWidth / 2f, trackLength = trackLength, isRtl = isRtl)
    }

    private fun valueAt(x: Float, trackLength: Float): Float {
        val position = (x - thumbWidth / 2f).coerceIn(0f, trackLength)
        val offset = position * state.range.span / trackLength
        val value = if (isRtl) state.range.maxValue - offset else state.range.minValue + offset
        return if (hasTickMarks) value.roundToLong().toFloat() else value
    }

}

/** Runs the slider press actions and the focus request of the down event. */
internal class SliderPressHandler(
    private val pressStartActions: List<DivAction>?,
    private val pressEndActions: List<DivAction>?,
    private val localComponent: DivLocalComponent,
    private val focusRequester: FocusRequester?,
) {
    fun onPressStart() {
        focusRequester?.requestFocus()
        pressStartActions?.let { localComponent.handleActions(it, DivActionSource.GESTURE) }
    }

    fun onPressEnd() {
        pressEndActions?.let { localComponent.handleActions(it, DivActionSource.GESTURE) }
    }
}
