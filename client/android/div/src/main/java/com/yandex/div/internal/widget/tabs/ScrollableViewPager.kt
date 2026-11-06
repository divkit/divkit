package com.yandex.div.internal.widget.tabs

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.yandex.div.R
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.core.widget.ViewPager2Wrapper
import com.yandex.div.internal.widget.OnInterceptTouchEventListener
import com.yandex.div.internal.widget.OnInterceptTouchEventListenerHost

@InternalApi
internal class ScrollableViewPager @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : ViewPager2Wrapper(context, attrs), OnInterceptTouchEventListenerHost {

    private val pagerRecyclerView = requireNotNull(getRecyclerView())
    private val smoothScrollController = TabsSmoothScrollController(viewPager, pagerRecyclerView)
    private val swipeController = TabsSwipeController(
        this, viewPager, pagerRecyclerView, smoothScrollController,
    )
    private val pageChangeCallback = TabsPageChangeCallback(swipeController::updateUserInputEnabled)

    override var onInterceptTouchEventListener: OnInterceptTouchEventListener? = null

    var adapter: RecyclerView.Adapter<*>?
        get() = viewPager.adapter
        set(value) {
            viewPager.adapter = value
            pageChangeCallback.resetSelection(viewPager.currentItem)
        }

    val currentItem: Int
        get() = viewPager.currentItem

    var isScrollEnabled: Boolean
        get() = swipeController.isScrollEnabled
        set(value) {
            swipeController.isScrollEnabled = value
        }

    var disabledScrollPages: Set<Int>?
        get() = swipeController.disabledScrollPages
        set(value) {
            swipeController.disabledScrollPages = value
        }

    var isEdgeScrollEnabled: Boolean
        get() = swipeController.isEdgeScrollEnabled
        set(value) {
            swipeController.isEdgeScrollEnabled = value
        }

    init {
        viewPager.id = R.id.div_tabs_view_pager
        viewPager.overScrollMode = OVER_SCROLL_NEVER
        pagerRecyclerView.overScrollMode = OVER_SCROLL_NEVER
        viewPager.registerOnPageChangeCallback(pageChangeCallback)
    }

    fun addOnPageChangeListener(listener: ViewPager.OnPageChangeListener) {
        pageChangeCallback.add(listener)
    }

    fun scrollToItem(position: Int, duration: Int?) {
        // Selecting a header in response to onPageSelected must not restart the scroll.
        if (pageChangeCallback.isDispatchingSelection && position == viewPager.currentItem) {
            return
        }
        smoothScrollController.scrollToItem(position, duration)
    }

    fun cancelScroll() = smoothScrollController.cancelScroll()

    fun release() {
        smoothScrollController.release()
        adapter = null
        pageChangeCallback.clear()
        viewPager.setPageTransformer(null)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        swipeController.onTouchEventBeforeDispatch(event)
        val handled = super.dispatchTouchEvent(event)
        swipeController.onTouchEventAfterDispatch(event)
        return handled
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        val intercepted = onInterceptTouchEventListener?.onInterceptTouchEvent(this, event) == true
        return intercepted || super.onInterceptTouchEvent(event)
    }

    override fun canScrollHorizontally(direction: Int) = viewPager.canScrollHorizontally(direction)
}
