package com.yandex.divkit.demo.benchmark

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.GravityCompat
import com.yandex.div.core.ObserverList
import com.yandex.div.internal.KLog
import kotlin.math.max

class ProfilingLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val frameMetrics = AppendableFrameMetrics()
    private val listeners = ObserverList<FrameMetricsListener>()

    private val childView: View?
        get() = if (childCount == 0) null else getChildAt(0)

    init {
        listeners.addObserver(LogcatFrameMetricsListener())
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        if (childCount > 0) throw IllegalStateException("ProfilingLayout can host only one direct child")
        super.addView(child, index, params)
        frameMetrics.reset()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        frameMetrics.reset()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var maxWidth = suggestedMinimumWidth
        var maxHeight = suggestedMinimumHeight
        var childState = 0

        val exactlySpecs = MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY
                    && MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY
        var matchParentChild: View? = null

        val child = childView
        if (child != null && child.visibility != GONE) {
            val lp = child.layoutParams as MarginLayoutParams
            if (exactlySpecs || (lp.width != LayoutParams.MATCH_PARENT && lp.height != LayoutParams.MATCH_PARENT)) {
                val childWidthMeasureSpec = getDefaultChildWidthMeasureSpec(widthMeasureSpec, lp)
                val childHeightMeasureSpec = getDefaultChildHeightMeasureSpec(heightMeasureSpec, lp)

                dispatchMeasure(child, childWidthMeasureSpec, childHeightMeasureSpec)

                maxWidth = max(
                    maxWidth,
                    paddingLeft + paddingRight + child.measuredWidth + lp.leftMargin + lp.rightMargin
                )
                maxHeight = max(
                    maxHeight,
                    paddingTop + paddingBottom + child.measuredHeight + lp.topMargin + lp.bottomMargin
                )
                childState = child.measuredState
            } else {
                matchParentChild = child
            }
        }

        setMeasuredDimension(
            View.resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
            View.resolveSizeAndState(maxHeight, heightMeasureSpec, childState shl View.MEASURED_HEIGHT_STATE_SHIFT)
        )

        if (matchParentChild != null) {
            val lp = matchParentChild.layoutParams as MarginLayoutParams

            val childWidthMeasureSpec = if (lp.width == LayoutParams.MATCH_PARENT) {
                val width = max(0, measuredWidth - paddingLeft - paddingRight - lp.leftMargin - lp.rightMargin)
                MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY)
            } else {
                getDefaultChildWidthMeasureSpec(widthMeasureSpec, lp)
            }

            val childHeightMeasureSpec = if (lp.height == LayoutParams.MATCH_PARENT) {
                val height = max(0, measuredHeight - paddingTop - paddingBottom - lp.topMargin - lp.bottomMargin)
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
            } else {
                getDefaultChildWidthMeasureSpec(widthMeasureSpec, lp)
            }

            dispatchMeasure(matchParentChild, childWidthMeasureSpec, childHeightMeasureSpec)
        }
    }

    private fun getDefaultChildWidthMeasureSpec(parentWidthMeasureSpec: Int, childLayoutParams: MarginLayoutParams): Int {
        return getChildMeasureSpec(
            parentWidthMeasureSpec,
            paddingLeft + paddingRight + childLayoutParams.leftMargin + childLayoutParams.rightMargin,
            childLayoutParams.width
        )
    }

    private fun getDefaultChildHeightMeasureSpec(parentHeightMeasureSpec: Int, childLayoutParams: MarginLayoutParams): Int {
        return getChildMeasureSpec(
            parentHeightMeasureSpec,
            paddingTop + paddingBottom + childLayoutParams.topMargin + childLayoutParams.bottomMargin,
            childLayoutParams.height
        )
    }

    private fun dispatchMeasure(child: View, widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val metrics = profile {
            child.measure(widthMeasureSpec, heightMeasureSpec)
        }
        frameMetrics.appendMeasure(metrics.wallTime)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val layoutLeft = paddingLeft
        val layoutRight = right - left - paddingRight
        val layoutTop = paddingTop
        val layoutBottom = bottom - top - paddingBottom

        val child = childView
        if (child != null && child.visibility != GONE) {
            val lp = child.layoutParams as LayoutParams
            val childWidth = child.measuredWidth
            val childHeight = child.measuredHeight

            val gravity = if (lp.gravity == LayoutParams.UNSPECIFIED_GRAVITY) DEFAULT_CHILD_GRAVITY else lp.gravity
            val absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection)

            val childLeft = when (absoluteGravity and Gravity.HORIZONTAL_GRAVITY_MASK) {
                Gravity.LEFT -> layoutLeft + lp.leftMargin
                Gravity.RIGHT -> layoutRight - childWidth - lp.rightMargin
                Gravity.CENTER_HORIZONTAL -> (layoutLeft + layoutRight - childWidth) / 2 + lp.leftMargin - lp.rightMargin
                else -> layoutLeft + lp.leftMargin
            }

            val childTop = when (gravity and Gravity.VERTICAL_GRAVITY_MASK) {
                Gravity.TOP -> layoutTop + lp.topMargin
                Gravity.BOTTOM -> layoutBottom - childHeight - lp.bottomMargin
                Gravity.CENTER_VERTICAL -> (layoutTop + layoutBottom - childHeight) / 2 + lp.topMargin - lp.bottomMargin
                else -> layoutTop + lp.topMargin
            }

            dispatchLayout(child, childLeft, childTop, childLeft + childWidth, childTop + childHeight)
        }
    }

    private fun dispatchLayout(child: View, left: Int, top: Int, right: Int, bottom: Int) {
        val metrics = profile {
            child.layout(left, top, right, bottom)
        }
        frameMetrics.appendLayout(metrics.wallTime)
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        listeners.notify {
            onMetricsMeasured(this@ProfilingLayout, frameMetrics)
        }
        frameMetrics.reset()
    }

    override fun drawChild(canvas: Canvas, child: View, drawingTime: Long): Boolean {
        var invalidated = false
        val metrics = profile {
            invalidated = super.drawChild(canvas, child, drawingTime)
        }
        frameMetrics.appendDraw(metrics.wallTime)
        return invalidated
    }

    fun addListener(listener: FrameMetricsListener) {
        listeners.addObserver(listener)
    }

    fun removeListener(listener: FrameMetricsListener) {
        listeners.removeObserver(listener)
    }

    private fun Iterable<FrameMetricsListener>.notify(action: FrameMetricsListener.() -> Unit) {
        forEach { listener ->
            listener.action()
        }
    }

    interface FrameMetrics {
        val measure: Long
        val layout: Long
        val draw: Long
    }

    private class AppendableFrameMetrics : FrameMetrics {

        override var measure: Long = 0L
            private set

        override var layout: Long = 0L
            private set

        override var draw: Long = 0L
            private set

        fun appendMeasure(duration: Long) {
            measure += duration
        }

        fun appendLayout(duration: Long) {
            layout += duration
        }

        fun appendDraw(duration: Long) {
            draw += duration
        }

        fun reset() {
            measure = 0L
            layout = 0L
            draw = 0L
        }
    }

    interface FrameMetricsListener {
        fun onMetricsMeasured(layout: ProfilingLayout, metrics: FrameMetrics)
    }

    private class LogcatFrameMetricsListener : FrameMetricsListener {

        override fun onMetricsMeasured(layout: ProfilingLayout, metrics: FrameMetrics) {
            KLog.d(TAG) { "measure = ${metrics.measure} ms, layout = ${metrics.layout} ms, draw = ${metrics.draw} ms" }
        }
    }

    private companion object {
        private const val TAG = "ProfilingLayout"
        private const val DEFAULT_CHILD_GRAVITY = Gravity.TOP and GravityCompat.START
    }
}
