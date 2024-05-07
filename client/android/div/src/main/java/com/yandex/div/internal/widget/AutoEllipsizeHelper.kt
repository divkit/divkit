package com.yandex.div.internal.widget

import android.view.ViewTreeObserver

/**
 * Helper to calculate and update max lines for given [textView].
 */
internal class AutoEllipsizeHelper(private val textView: EllipsizedTextView) {

    /**
     * If auto ellipsize is enabled.
     */
    var isEnabled = false

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
        preDrawListener = ViewTreeObserver.OnPreDrawListener {
            if (!isEnabled) {
                return@OnPreDrawListener true
            }
            val visibleLineCount = textView.run {
                val textHeight = height - compoundPaddingTop - compoundPaddingBottom
                val lastVisibleLine = lineAt(textHeight)
                if (textHeight >= textHeight(lastVisibleLine + 1)) lastVisibleLine + 1 else lastVisibleLine
            }
            if (visibleLineCount < textView.lineCount) {
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
}
