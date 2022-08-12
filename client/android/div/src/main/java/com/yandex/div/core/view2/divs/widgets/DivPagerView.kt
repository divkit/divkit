package com.yandex.div.core.view2.divs.widgets

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import com.yandex.div.core.Disposable
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.expression.ExpressionSubscriber
import com.yandex.div.core.view2.divs.drawChildrenShadows
import com.yandex.div.core.widget.ViewPager2Wrapper
import com.yandex.div.core.widget.invalidateAfter
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.view.OnInterceptTouchEventListener
import com.yandex.div.view.OnInterceptTouchEventListenerHost
import com.yandex.div2.DivBorder
import com.yandex.div2.DivPager

@Mockable
internal class DivPagerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewPager2Wrapper(context, attrs, defStyleAttr), DivBorderSupports,
    OnInterceptTouchEventListenerHost,
    TransientView,
    ExpressionSubscriber {

    internal var div: DivPager? = null

    override var onInterceptTouchEventListener: OnInterceptTouchEventListener? = null
    internal var currentItem: Int
        get() = viewPager.currentItem
        set(value) = viewPager.setCurrentItem(value, false)

    private var borderDrawer: DivBorderDrawer? = null
    override val border: DivBorder?
        get() = borderDrawer?.border

    override fun getDivBorderDrawer() = borderDrawer

    override var isTransient = false
        set(value) = invalidateAfter {
            field = value
        }

    override val subscriptions = mutableListOf<Disposable>()

    private var isDrawing = false

    override fun setBorder(border: DivBorder?, resolver: ExpressionResolver) {
        if (border == borderDrawer?.border) return

        borderDrawer?.release()
        borderDrawer = border?.let {
            DivBorderDrawer(resources.displayMetrics, this, resolver, border)
        }
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        borderDrawer?.onBoundsChanged(w, h)
    }

    override fun draw(canvas: Canvas) {
        isDrawing = true
        borderDrawer.drawClipped(canvas) { super.draw(canvas) }
        isDrawing = false
    }

    override fun dispatchDraw(canvas: Canvas) {
        drawChildrenShadows(canvas)

        if (isDrawing) {
            super.dispatchDraw(canvas)
        } else {
            borderDrawer.drawClipped(canvas) { super.dispatchDraw(canvas) }
        }
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        val intercepted = onInterceptTouchEventListener?.onInterceptTouchEvent(target = this, event = event) ?: false
        return intercepted || super.onInterceptTouchEvent(event)
    }

    override fun release() {
        super.release()
        borderDrawer?.release()
    }
}
