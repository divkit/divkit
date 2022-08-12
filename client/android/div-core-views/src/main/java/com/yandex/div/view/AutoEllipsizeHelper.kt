package com.yandex.div.view

import android.view.ViewTreeObserver
import kotlin.math.max
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
            val textLayout = textView.layout ?: return@OnPreDrawListener true
            val maxLines = textView.run {
                // Sometimes, TextView height is less then "completely_visible_lines" * "line_height"
                // and sometimes it's not.
                // For example, if we set lineSpacingExtra, it won't be applied for the last line.
                // Also textView has inner paddings like FirstBaselineToTopHeight, which makes
                // calculating the number of fully visible lines nearly impossible.
                var visibleLines = min(textLayout.lineCount, height / lineHeight + 1)

                // So our visibleLines variable is more like an upper bound, we should
                // manually check if the last visible line is actually completely visible
                while (visibleLines > 0) {
                    val visibleTextHeight = textLayout.getLineBottom(visibleLines - 1)
                    val availableHeight = height - paddingTop - paddingBottom
                    if (visibleTextHeight - availableHeight <= ALLOWED_TEXT_OVERFLOW_PX) {
                        break
                    }
                    visibleLines -= 1
                }
                max(0, visibleLines)
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

    private companion object {
        /**
         * Allowed threshold in pixels height is overflowed by text.
         */
        private const val ALLOWED_TEXT_OVERFLOW_PX = 3
    }
}
