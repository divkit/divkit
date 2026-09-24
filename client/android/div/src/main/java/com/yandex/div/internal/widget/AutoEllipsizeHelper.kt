package com.yandex.div.internal.widget

import android.view.ViewTreeObserver
import com.yandex.div.internal.view.DrawingPassOverrideStrategy
import com.yandex.div.internal.view.onPreDrawListener
import com.yandex.div.internal.KLog

/**
 * Helper to calculate and update max lines for given [textView].
 */
internal class AutoEllipsizeHelper(private val textView: EllipsizedTextView) {

    /**
     * If auto ellipsize is enabled.
     */
    var isEnabled = false
        set(value) {
            if (field == value) {
                return
            }
            field = value
            lastMeasurementInputs = null
            if (!value) {
                removeListener()
            }
        }

    var drawingPassOverrideStrategy: DrawingPassOverrideStrategy = DrawingPassOverrideStrategy.Safe

    private var preDrawListener: ViewTreeObserver.OnPreDrawListener? = null
    private var lastMeasurementInputs: MeasurementInputs? = null

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

    fun onViewMeasured(textRevision: Int) {
        if (!isEnabled || !textView.isAttachedToWindow) {
            return
        }

        val measurementInputs = MeasurementInputs(
            measuredWidth = textView.measuredWidth,
            measuredHeight = textView.measuredHeight,
            maxLines = textView.maxLines,
            sourceLineCount = textView.untruncatedLineCount,
            layoutHeight = textView.layout?.height,
            textRevision = textRevision,
        )
        if (measurementInputs == lastMeasurementInputs) {
            return
        }

        lastMeasurementInputs = measurementInputs
        addListener()
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
            val maxLines = textView.maxLines
            val canReduceMaxLines = maxLines < 0 || visibleLineCount < maxLines
            if (visibleLineCount > 0 && visibleLineCount < textView.untruncatedLineCount &&
                canReduceMaxLines
            ) {
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

    private data class MeasurementInputs(
        val measuredWidth: Int,
        val measuredHeight: Int,
        val maxLines: Int,
        val sourceLineCount: Int,
        val layoutHeight: Int?,
        val textRevision: Int,
    )
}
