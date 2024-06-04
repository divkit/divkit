package com.yandex.div.core.view2.divs.widgets

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import androidx.recyclerview.widget.RecyclerViewAccessibilityDelegate
import androidx.viewpager2.widget.ViewPager2
import com.yandex.div.R
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.view2.divs.PagerSelectedActionsDispatcher
import com.yandex.div.core.view2.divs.drawChildrenShadows
import com.yandex.div.core.widget.ViewPager2Wrapper
import com.yandex.div.internal.widget.OnInterceptTouchEventListener
import com.yandex.div.internal.widget.OnInterceptTouchEventListenerHost
import com.yandex.div2.DivPager

@Mockable
internal class DivPagerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewPager2Wrapper(context, attrs, defStyleAttr),
    DivHolderView<DivPager> by DivHolderViewMixin(),
    OnInterceptTouchEventListenerHost {

    internal var changePageCallbackForState: ViewPager2.OnPageChangeCallback? = null
        set(value) {
            field?.let(viewPager::unregisterOnPageChangeCallback)
            value?.let(viewPager::registerOnPageChangeCallback)
            field = value
        }

    internal var changePageCallbackForLogger: ViewPager2.OnPageChangeCallback? = null
        set(value) {
            field?.let(viewPager::unregisterOnPageChangeCallback)
            value?.let(viewPager::registerOnPageChangeCallback)
            field = value
        }

    internal var pagerSelectedActionsDispatcher: PagerSelectedActionsDispatcher? = null
        set(value) {
            field?.detach(viewPager)
            value?.attach(viewPager)
            field = value
        }

    internal var pagerOnItemsCountChange: OnItemsUpdatedCallback? = null

    override var onInterceptTouchEventListener: OnInterceptTouchEventListener? = null

    internal var currentItem: Int
        get() = viewPager.currentItem
        set(value) = viewPager.setCurrentItem(value, false)

    fun enableAccessibility() {
        val recycler = getRecyclerView() ?: return
        if (recycler.compatAccessibilityDelegate != null) return
        val accessibilityDelegateCompat = object : RecyclerViewAccessibilityDelegate(recycler) {
            override fun onRequestSendAccessibilityEvent(
                host: ViewGroup?, child: View?, event: AccessibilityEvent?
            ): Boolean {
                if (child != null && event?.eventType == AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED) {
                    (child.getTag(R.id.div_pager_item_clip_id) as Int?)?.let { pos ->
                        viewPager.adapter?.let { adapter ->
                            if (pos >= 0 && pos < adapter.itemCount) {
                                currentItem = pos
                            }
                        }
                    }
                }
                return super.onRequestSendAccessibilityEvent(host, child, event)
            }
        }

        recycler.setAccessibilityDelegateCompat(accessibilityDelegateCompat)
    }

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
        val intercepted =
            onInterceptTouchEventListener?.onInterceptTouchEvent(target = this, event = event)
                ?: false
        return intercepted || super.onInterceptTouchEvent(event)
    }

    fun getPageView(index: Int): View? {
        val recyclerView = getRecyclerView() ?: return null
        val wrappedChild = recyclerView.getChildAt(index) as? ViewGroup ?: return null
        return wrappedChild.getChildAt(0)
    }

    internal fun interface OnItemsUpdatedCallback {
        fun onItemsUpdated()
    }
}
