package com.yandex.div.core.tooltip

import android.content.Context
import android.graphics.Canvas
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.view2.divs.drawShadow
import com.yandex.div.internal.widget.DivLayoutParams
import com.yandex.div.internal.widget.FrameContainerLayout
import com.yandex.div.internal.widget.TransientView
import com.yandex.div.internal.widget.TransientViewMixin
import androidx.core.view.isNotEmpty
import androidx.core.view.isEmpty

@Mockable
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

    fun setTooltipPosition(x: Int, y: Int, width: Int, height: Int) =
        setChildPosition(tooltipView, x, y, width, height)

    fun setBringToTopPosition(x: Int, y: Int, width: Int, height: Int) =
        setChildPosition(bringToTopView, x, y, width, height)

    private fun setChildPosition(child: View?, x: Int, y: Int, width: Int, height: Int) =
        child?.updateLayoutParams<DivLayoutParams> {
            leftMargin = x
            topMargin = y
            this.width = width
            this.height = height
        }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        val result = super.onTouchEvent(ev)
        if (!result && ev.action == MotionEvent.ACTION_DOWN) {
            dismissAction(ev)
        }
        return result
    }
}
