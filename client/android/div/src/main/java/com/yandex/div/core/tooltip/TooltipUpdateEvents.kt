package com.yandex.div.core.tooltip

import android.os.Handler
import android.view.View
import com.yandex.div.core.Disposable

/** Tracks window and anchor layout changes that require tooltip position updates. */
internal class TooltipUpdateEvents(
    private val rootView: View,
    private val anchor: View,
    private val handler: Handler,
) {

    fun subscribeToUpdates(
        onEvent: () -> Unit,
    ): Disposable {
        val windowBoundsListener = View.OnLayoutChangeListener {
                _, left, top, right, bottom,
                oldLeft, oldTop, oldRight, oldBottom ->
            val widthChanged = right - left != oldRight - oldLeft
            val heightChanged = bottom - top != oldBottom - oldTop

            if (widthChanged || heightChanged) {
                onEvent()
            }
        }
        rootView.addOnLayoutChangeListener(windowBoundsListener)

        return Disposable {
            rootView.removeOnLayoutChangeListener(windowBoundsListener)
        }
    }

    fun subscribeToAnchorUpdates(
        onEvent: () -> Unit,
    ): Disposable {
        return TooltipAnchorTracker(
            anchor = anchor,
            handler = handler,
            onAnchorBoundsChanged = onEvent,
        )
    }

}
