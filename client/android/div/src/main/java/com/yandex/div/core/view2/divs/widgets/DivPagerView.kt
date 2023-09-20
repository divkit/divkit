package com.yandex.div.core.view2.divs.widgets

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import com.yandex.div.core.Disposable
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.view2.divs.drawChildrenShadows
import com.yandex.div.core.widget.ViewPager2Wrapper
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.internal.widget.OnInterceptTouchEventListener
import com.yandex.div.internal.widget.OnInterceptTouchEventListenerHost
import com.yandex.div.internal.widget.TransientView
import com.yandex.div.internal.widget.TransientViewMixin
import com.yandex.div2.DivPager

@Mockable
internal class DivPagerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewPager2Wrapper(context, attrs, defStyleAttr), DivBorderSupports by DivBorderSupportsMixin(),
        OnInterceptTouchEventListenerHost, TransientView by TransientViewMixin(), ExpressionSubscriber {

    internal var div: DivPager? = null

    override var onInterceptTouchEventListener: OnInterceptTouchEventListener? = null
    internal var currentItem: Int
        get() = viewPager.currentItem
        set(value) = viewPager.setCurrentItem(value, false)

    override val subscriptions = mutableListOf<Disposable>()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        onBoundsChanged(w, h)
    }

    override fun draw(canvas: Canvas) {
        drawBorderClipped(canvas) { super.draw(it) }
    }

    override fun dispatchDraw(canvas: Canvas) {
        drawChildrenShadows(canvas)
        dispatchDrawBorderClipped(canvas) { super.dispatchDraw(it) }
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        val intercepted = onInterceptTouchEventListener?.onInterceptTouchEvent(target = this, event = event) ?: false
        return intercepted || super.onInterceptTouchEvent(event)
    }

    override fun release() {
        super.release()
        releaseBorderDrawer()
    }
}
