package com.yandex.div.internal.widget

import android.view.ViewTreeObserver
import com.yandex.div.core.widget.DrawingPassOverrideStrategy
import com.yandex.div.core.widget.onPreDrawListener
import com.yandex.div.internal.KLog

/**
 * Helper to calculate and update max lines for given [textView].
 */
internal class AutoEllipsizeHelper(private val textView: EllipsizedTextView) {

    /**
     * If auto ellipsize is enabled.
     */
    var isEnabled = false

    var drawingPassOverrideStrategy: DrawingPassOverrideStrategy = DrawingPassOverrideStrategy.Default

    private var preDrawListener: ViewTreeObserver.OnPreDrawListener? = null

    /**
     * Called when [textView] is attached to window.
     */
    fun onViewAttachedToWindow() {
        if (isEnabled) {
            addListener()
        }
    }

    /**
     * Called when [textView] detached from window.
     */
    fun onViewDetachedFromWindow() {
        removeListener()
    }

    private fun addListener() {
        if (preDrawListener != null) {
            return
        }
        preDrawListener = onPreDrawListener(drawingPassOverrideStrategy) {
            if (!isEnabled) {
                return@onPreDrawListener true
            }
            val visibleLineCount = textView.run {
                val textHeight = height - compoundPaddingTop - compoundPaddingBottom
                val lastVisibleLine = lineAt(textHeight)
                if (textHeight >= textHeight(lastVisibleLine + 1)) lastVisibleLine + 1 else lastVisibleLine
            }
            if (visibleLineCount > 0 && visibleLineCount < textView.lineCount) {
                KLog.d(TAG) { "Trying to set new max lines $visibleLineCount. Current drawing pass is canceled. " }
                textView.maxLines = visibleLineCount
                false
            } else {
                removeListener()
                true
            }
        }
        textView.viewTreeObserver.addOnPreDrawListener(preDrawListener)
    }

    private fun removeListener() {
        if (preDrawListener != null) {
            textView.viewTreeObserver.removeOnPreDrawListener(preDrawListener)
            preDrawListener = null
        }
    }

    private companion object {
        const val TAG = "AutoEllipsizeHelper"
    }
}
