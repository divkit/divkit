package com.yandex.div.core.tooltip

import android.os.Handler
import android.view.View
import android.widget.PopupWindow
import androidx.annotation.VisibleForTesting
import com.yandex.div.core.Disposable
import com.yandex.div.internal.view.onPreDrawListener

@VisibleForTesting
internal const val ANCHOR_TRACKING_DURATION_MS = 1_000L

/**
 * Observes a tooltip anchor's layout/pre-draw for a limited time and notifies
 * when its window position or size changes (e.g. after configuration change).
 */
internal class TooltipAnchorTracker(
    private val tooltip: TooltipData,
    private val popupWindow: PopupWindow,
    private val handler: Handler,
    private val onAnchorPositionChanged: () -> Unit,
) : Disposable {

    private val anchor = tooltip.anchor
    private val location = IntArray(2)
    private var lastX = Int.MIN_VALUE
    private var lastY = Int.MIN_VALUE
    private var lastWidth = -1
    private var lastHeight = -1
    private var closed = false

    private val layoutListener = View.OnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
        updateIfNeeded()
    }
    private val preDrawListener = onPreDrawListener {
        updateIfNeeded()
        true
    }
    private val viewTreeObserver = anchor.viewTreeObserver
    private val timeoutRunnable = Runnable { close() }

    init {
        anchor.addOnLayoutChangeListener(layoutListener)
        if (viewTreeObserver.isAlive) {
            viewTreeObserver.addOnPreDrawListener(preDrawListener)
        }
        handler.postDelayed(timeoutRunnable, ANCHOR_TRACKING_DURATION_MS)
        updateIfNeeded()
    }

    private fun updateIfNeeded() {
        if (closed || tooltip.dismissed || !popupWindow.isShowing) return
        anchor.getLocationInWindow(location)
        val width = anchor.width
        val height = anchor.height
        if (location[0] == lastX && location[1] == lastY &&
            width == lastWidth && height == lastHeight
        ) {
            return
        }
        lastX = location[0]
        lastY = location[1]
        lastWidth = width
        lastHeight = height
        onAnchorPositionChanged()
    }

    override fun close() {
        if (closed) return
        closed = true
        anchor.removeOnLayoutChangeListener(layoutListener)
        if (viewTreeObserver.isAlive) {
            viewTreeObserver.removeOnPreDrawListener(preDrawListener)
        }
        handler.removeCallbacks(timeoutRunnable)
    }
}
