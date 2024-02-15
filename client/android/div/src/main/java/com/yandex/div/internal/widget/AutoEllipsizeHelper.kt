package com.yandex.div.internal.widget

import android.view.ViewTreeObserver
import com.yandex.div.core.widget.FixedLineHeightView.Companion.UNDEFINED_LINE_HEIGHT
import kotlin.math.min

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
            addPreDrawListener()
        }
    }

    /**
     * Called when [textView] detached from window.
     */
    fun onViewDetachedFromWindow() {
        removePreDrawListener()
    }

    private fun addPreDrawListener() {
        if (preDrawListener != null) {
            return
        }
        preDrawListener = ViewTreeObserver.OnPreDrawListener {
            if (!isEnabled) {
                return@OnPreDrawListener true
            }
            val maxLines = textView.run {
                val lineHeight = if (fixedLineHeight != UNDEFINED_LINE_HEIGHT) fixedLineHeight else lineHeight
                min(lineCount, height / lineHeight)
            }
            if (maxLines != textView.maxLines) {
                textView.maxLines = maxLines
                false
            } else {
                removePreDrawListener()
                true
            }
        }
        textView.viewTreeObserver.addOnPreDrawListener(preDrawListener)
    }

    private fun removePreDrawListener() {
        if (preDrawListener != null) {
            textView.viewTreeObserver.removeOnPreDrawListener(preDrawListener)
            preDrawListener = null
        }
    }
}
