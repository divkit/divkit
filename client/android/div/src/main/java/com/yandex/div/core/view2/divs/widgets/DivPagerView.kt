package com.yandex.div.core.view2.divs.widgets

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerViewAccessibilityDelegate
import androidx.viewpager2.widget.ViewPager2
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.view2.divs.drawShadow
import com.yandex.div.core.view2.divs.pager.PagerSelectedActionsDispatcher
import com.yandex.div.core.widget.DivViewWrapper
import com.yandex.div.core.widget.ViewPager2Wrapper
import com.yandex.div.internal.widget.OnInterceptTouchEventListener
import com.yandex.div.internal.widget.OnInterceptTouchEventListenerHost
import com.yandex.div2.Div

@Mockable
internal class DivPagerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewPager2Wrapper(context, attrs, defStyleAttr),
    DivHolderView<Div.Pager> by DivHolderViewMixin(),
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

    internal var changePageCallbackForOffScreenPages: OffScreenPagesUpdateCallback? = null
        set(value) {
            field?.let {
                viewPager.unregisterOnPageChangeCallback(it)
                getRecyclerView()?.removeOnLayoutChangeListener(it)
            }
            value?.let {
                viewPager.registerOnPageChangeCallback(it)
                getRecyclerView()?.addOnLayoutChangeListener(it)
            }
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

        object : RecyclerViewAccessibilityDelegate(recycler) {
            override fun onRequestSendAccessibilityEvent(
                host: ViewGroup,
                child: View,
                event: AccessibilityEvent
            ): Boolean {
                performActionIfNeeded(child, event)
                return super.onRequestSendAccessibilityEvent(host, child, event)
            }

            private fun performActionIfNeeded(child: View, event: AccessibilityEvent) {
                if (event.eventType != AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED) return
                val pos = getWrapperFor(child)?.let { recycler.getChildAdapterPosition(it) } ?: return
                if (currentItem == pos || pos == RecyclerView.NO_POSITION) return

                val action = if (pos > currentItem) {
                    AccessibilityNodeInfo.ACTION_SCROLL_FORWARD
                } else {
                    AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD
                }
                recycler.performAccessibilityAction(action, null)
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

    override fun drawChild(canvas: Canvas, child: View?, drawingTime: Long): Boolean {
        if (child != null && child.isVisible) {
            child.drawShadow(canvas)
        }
        return super.drawChild(canvas, child, drawingTime)
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

    private fun getWrapperFor(child: View): View? {
        var parent = child
        while (parent != getRecyclerView()) {
            if (parent is DivViewWrapper) return parent
            parent = parent.parent as? View ?: return null
        }
        return null
    }

    internal fun interface OnItemsUpdatedCallback {
        fun onItemsUpdated()
    }

    internal abstract class OffScreenPagesUpdateCallback : ViewPager2.OnPageChangeCallback(), OnLayoutChangeListener {
        override fun onLayoutChange(
            v: View?, left: Int, top: Int, right: Int, bottom: Int,
            oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int
        ) = Unit
    }
}
