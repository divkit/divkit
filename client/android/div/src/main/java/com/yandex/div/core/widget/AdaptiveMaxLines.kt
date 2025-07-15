package com.yandex.div.core.widget

import android.text.TextUtils
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.core.view.ViewCompat
import com.yandex.div.core.widget.AdaptiveMaxLines.Params

/**
 * Adjust [TextView] max lines based on [Params].
 */
internal class AdaptiveMaxLines(
    private val textView: TextView,
    private val drawingPassOverrideStrategy: DrawingPassOverrideStrategy,
) {

    private var viewAttachListener: View.OnAttachStateChangeListener? = null
    private var preDrawListener: ViewTreeObserver.OnPreDrawListener? = null

    private var params: Params? = null

    private var isAdaptLinesRequested = false

    /**
     * Apply given [params] to adjust [TextView.getMaxLines].
     */
    fun apply(params: Params) {
        if (this.params == params) {
            return
        }
        this.params = params
        if (ViewCompat.isAttachedToWindow(textView)) {
            addPreDrawListener()
        }
        addAttachListener()
    }

    /**
     * Resets all listeners.
     */
    fun reset() {
        removeAttachListener()
        removePreDrawListener()
    }

    private fun addAttachListener() {
        if (viewAttachListener != null) {
            return
        }
        viewAttachListener = object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) = addPreDrawListener()
            override fun onViewDetachedFromWindow(v: View) = removePreDrawListener()
        }.apply(textView::addOnAttachStateChangeListener)
    }

    private fun removeAttachListener() {
        viewAttachListener?.let(textView::removeOnAttachStateChangeListener)
        viewAttachListener = null
    }

    private fun addPreDrawListener() {
        if (preDrawListener != null) {
            return
        }
        preDrawListener = onPreDrawListener(drawingPassOverrideStrategy) {
            val params = params ?: return@onPreDrawListener true

            if (TextUtils.isEmpty(textView.text)) {
                return@onPreDrawListener true
            }

            if (isAdaptLinesRequested) {
                removePreDrawListener()
                isAdaptLinesRequested = false
                return@onPreDrawListener true
            }

            val maxLines =
                Int.MAX_VALUE.takeIf { textView.lineCount <= params.totalVisibleLines } ?: params.maxLines
            return@onPreDrawListener if (maxLines != textView.maxLines) {
                textView.maxLines = maxLines
                isAdaptLinesRequested = true
                false
            } else {
                removePreDrawListener()
                true
            }
        }.apply(textView.viewTreeObserver::addOnPreDrawListener)
    }

    private fun removePreDrawListener() {
        preDrawListener?.let(textView.viewTreeObserver::removeOnPreDrawListener)
        preDrawListener = null
    }

    data class Params(
        /**
         * Maximum lines.
         *
         * @see TextView.getMaxLines
         */
        val maxLines: Int,

        /**
         * Minimum number of lines to be hidden, when text is ellipsized.
         *
         * @see TextView.setEllipsize
         */
        val minHiddenLines: Int
    ) {

        val totalVisibleLines: Int
            get() = maxLines + minHiddenLines
    }
}
