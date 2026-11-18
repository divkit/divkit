package com.yandex.div.compose.tooltips

import android.view.MotionEvent
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

internal val LocalTooltipTouchDispatcher = staticCompositionLocalOf<((MotionEvent) -> Boolean)?> { null }

@Composable
internal fun Modifier.tooltipInput(onTap: () -> Unit = {}): Modifier {
    val dispatchToUnderlyingWindow = LocalTooltipTouchDispatcher.current
    return pointerInput(dispatchToUnderlyingWindow, onTap) {
        if (dispatchToUnderlyingWindow == null) {
            detectTapGestures(onTap = { onTap() })
            return@pointerInput
        }
        awaitEachGesture {
            awaitFirstDown().consume()
            val forwarder = TooltipTouchForwarder(dispatchToUnderlyingWindow)
            try {
                do {
                    currentEvent.motionEvent?.let(forwarder::dispatch)
                    if (currentEvent.changes.none { it.pressed }) {
                        currentEvent.changes.forEach { it.consume() }
                        if (currentEvent.motionEvent?.actionMasked == MotionEvent.ACTION_UP) {
                            onTap()
                        }
                        break
                    }
                    awaitPointerEvent()
                } while (currentEvent.changes.none { it.isConsumed })
            } finally {
                forwarder.dispose()
            }
        }
    }
}

private class TooltipTouchForwarder(private val dispatchEvent: (MotionEvent) -> Boolean) {
    private var lastEvent: MotionEvent? = null

    fun dispatch(event: MotionEvent) {
        lastEvent?.recycle()
        lastEvent = MotionEvent.obtain(event)
        dispatchEvent(event)
    }

    fun dispose() {
        val event = lastEvent ?: return
        if (event.actionMasked != MotionEvent.ACTION_UP && event.actionMasked != MotionEvent.ACTION_CANCEL) {
            event.action = MotionEvent.ACTION_CANCEL
            dispatchEvent(event)
        }
        event.recycle()
        lastEvent = null
    }
}
