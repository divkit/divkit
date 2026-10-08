package com.yandex.div.core.tooltip

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.view.isEmpty
import androidx.core.view.isNotEmpty
import androidx.core.view.isVisible
import com.yandex.div.core.view2.divs.drawShadow
import com.yandex.div.internal.widget.FrameContainerLayout
import com.yandex.div.internal.widget.TransientView
import com.yandex.div.internal.widget.TransientViewMixin

internal class DivTooltipContainer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameContainerLayout(context, attrs, defStyleAttr), TransientView by TransientViewMixin() {

    init {
        clipChildren = false
        clipToPadding = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            defaultFocusHighlightEnabled = false
        }
        importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO
    }

    var dismissAction: (event: MotionEvent) -> Unit = {}

    private var hasSubstrateView = false
    private var hasBringToTopView = false
    // Preserve controller-calculated bounds across later layout passes driven by PopupWindow.
    private var tooltipPosition: Rect? = null
    private var bringToTopPosition: Rect? = null

    val substrateView: View?
        get() = if (hasSubstrateView && isNotEmpty()) getChildAt(0) else null

    val bringToTopView: View?
        get() = if (hasBringToTopView && hasSubstrateView && childCount > 1) getChildAt(1) else null

    val tooltipView: View?
        get() = if (isEmpty()) null else getChildAt(childCount - 1)

    override fun drawChild(canvas: Canvas, child: View?, drawingTime: Long): Boolean {
        if (child != null && child.isVisible) {
            child.drawShadow(canvas)
        }
        return super.drawChild(canvas, child, drawingTime)
    }

    fun setViews(substrate: View?, bringToTop: View?, tooltip: View) {
        removeAllViews()
        hasSubstrateView = false
        hasBringToTopView = false
        // Replacement children must not inherit positions calculated for the previous content.
        tooltipPosition = null
        bringToTopPosition = null

        substrate?.let {
            hasSubstrateView = true
            addView(it)
        }

        if (hasSubstrateView) {
            bringToTop?.let {
                hasBringToTopView = true
                addView(it)
            }
        }

        addView(tooltip)
    }

    fun setTooltipPosition(x: Int, y: Int, width: Int, height: Int) {
        val position = Rect(x, y, x + width, y + height)
        tooltipPosition = position
        tooltipView?.requestLayout()
    }

    fun setBringToTopPosition(x: Int, y: Int, width: Int, height: Int) {
        val position = Rect(x, y, x + width, y + height)
        bringToTopPosition = position
        bringToTopView?.requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // PopupWindow may trigger another measurement with the original LayoutParams after the
        // controller has constrained the tooltip to the visible window. Keep the measured size
        // consistent with the tracked bounds restored in onLayout.
        tooltipPosition?.let { tooltipView?.measure(it) }
        // The bring-to-top view keeps the source LayoutParams, whose constraints this container
        // resolves against its own full-window bounds. Restore the tracked source size as well.
        bringToTopPosition?.let { bringToTopView?.measure(it) }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        // A later popup layout resolves gravity against the container and can overwrite the
        // anchor-relative bounds calculated by the controller. Restore those tracked bounds.
        tooltipPosition?.let { tooltipView?.layout(it) }
        // As with measurement, DivTooltipContainer resolves the bring-to-top replica's
        // LayoutParams against its own bounds. Restore the tracked source bounds afterwards.
        bringToTopPosition?.let { bringToTopView?.layout(it) }
    }

    private fun View.layout(position: Rect) = layout(position.left, position.top, position.right, position.bottom)

    private fun View.measure(position: Rect) = measure(
        MeasureSpec.makeMeasureSpec(position.width(), MeasureSpec.EXACTLY),
        MeasureSpec.makeMeasureSpec(position.height(), MeasureSpec.EXACTLY),
    )

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        val result = super.onTouchEvent(ev)
        if (!result && ev.action == MotionEvent.ACTION_DOWN) {
            dismissAction(ev)
        }
        return result
    }
}
