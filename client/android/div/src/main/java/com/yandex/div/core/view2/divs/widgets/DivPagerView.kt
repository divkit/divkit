package com.yandex.div.core.view2.divs.widgets

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.recyclerview.widget.RecyclerViewAccessibilityDelegate
import androidx.viewpager2.widget.ViewPager2
import com.yandex.div.R
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.view2.divs.drawChildrenShadows
import com.yandex.div.core.view2.divs.pager.PagerSelectedActionsDispatcher
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

    private val changePageCallbacksForIndicators:
        MutableList<ViewPager2.OnPageChangeCallback> = mutableListOf()

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

    internal var clipToPage: Boolean
        get() = getRecyclerView()?.clipChildren ?: false
        set(value) {
            getRecyclerView()?.clipChildren = value
        }

    override var onInterceptTouchEventListener: OnInterceptTouchEventListener? = null

    internal var currentItem: Int
        get() = viewPager.currentItem
        set(value) = viewPager.setCurrentItem(value, false)

    private val accessibilityDelegate by lazy(LazyThreadSafetyMode.NONE) {
        val recycler = getRecyclerView() ?: return@lazy null
        recycler.descendantFocusability = ViewGroup.FOCUS_AFTER_DESCENDANTS

        object : RecyclerViewAccessibilityDelegate(recycler) {
            override fun onRequestSendAccessibilityEvent(
                host: ViewGroup, child: View, event: AccessibilityEvent
            ): Boolean {
                if (event.eventType == AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED) {
                    getFocusedChildPos(child)?.let { pos ->
                        if (currentItem != pos) {
                            recycler.performAccessibilityAction(
                                if (pos > currentItem) AccessibilityNodeInfo.ACTION_SCROLL_FORWARD
                                else AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD,
                                null
                            )
                        }
                    }
                }
                return super.onRequestSendAccessibilityEvent(host, child, event)
            }
        }
    }

    fun enableAccessibility() {
        accessibilityDelegate?.let {
            getRecyclerView()?.setAccessibilityDelegateCompat(it)
        }
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

    fun addChangePageCallbackForIndicators(callback: ViewPager2.OnPageChangeCallback) {
        changePageCallbacksForIndicators.add(callback)
        viewPager.registerOnPageChangeCallback(callback)
    }

    fun removeChangePageCallbackForIndicators(callback: ViewPager2.OnPageChangeCallback) {
        changePageCallbacksForIndicators.remove(callback)
        viewPager.unregisterOnPageChangeCallback(callback)
    }

    fun clearChangePageCallbackForIndicators() {
        changePageCallbacksForIndicators.forEach {
            viewPager.unregisterOnPageChangeCallback(it)
        }
        changePageCallbacksForIndicators.clear()
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

    private fun getFocusedChildPos(child: View): Int? {
        var child = child
        while (child != this) {
            (child.getTag(R.id.div_pager_item_clip_id) as? Int)?.let { return it }
            child = child.parent as? View ?: return null
        }
        return null
    }

    internal fun interface OnItemsUpdatedCallback {
        fun onItemsUpdated()
    }
}
