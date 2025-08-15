package com.yandex.div.internal.widget.tabs

import android.content.Context
import android.util.AttributeSet
import android.util.LayoutDirection
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.yandex.div.core.util.isLayoutRtl


internal open class RtlViewPager(context: Context, attrs: AttributeSet?) : ViewPager(context, attrs) {

    private val pageChangeListeners = hashMapOf<OnPageChangeListener, ReversingOnPageChangeListener>()

    override fun getCurrentItem(): Int {
        var item = super.getCurrentItem()
        val adapter = super.getAdapter()
        if (adapter != null && isLayoutRtl()) {
            item = adapter.count - item - 1
        }
        return item
    }

    override fun setCurrentItem(position: Int, smoothScroll: Boolean) {
        var pos = position
        val adapter = super.getAdapter()
        if (adapter != null && isLayoutRtl()) {
            pos = adapter.count - pos - 1
        }
        super.setCurrentItem(pos, smoothScroll)
    }

    override fun setCurrentItem(position: Int) {
        var pos = position
        val adapter = super.getAdapter()
        if (adapter != null && isLayoutRtl()) {
            pos = adapter.count - pos - 1
        }
        super.setCurrentItem(pos)
    }

    override fun addOnPageChangeListener(listener: OnPageChangeListener) {
        val reversingListener = ReversingOnPageChangeListener(listener)
        pageChangeListeners[listener] = reversingListener
        super.addOnPageChangeListener(reversingListener)
    }

    override fun removeOnPageChangeListener(listener: OnPageChangeListener) {
        val reverseListener: ReversingOnPageChangeListener? = pageChangeListeners.remove(listener)
        if (reverseListener != null) {
            super.removeOnPageChangeListener(reverseListener)
        }
    }

    override fun clearOnPageChangeListeners() {
        super.clearOnPageChangeListeners()
        pageChangeListeners.clear()
    }

    inner class ReversingOnPageChangeListener internal constructor(private val listener: OnPageChangeListener) : OnPageChangeListener {
        override fun onPageScrolled(pos: Int, posOffset: Float, posOffsetPixels: Int) {
            var position = pos
            var positionOffset = posOffset
            var positionOffsetPixels = posOffsetPixels
            val adapter: PagerAdapter? = super@RtlViewPager.getAdapter()
            if (isLayoutRtl() && adapter != null) {
                val count = adapter.count
                var remainingWidth = (width * (1 - adapter.getPageWidth(position))).toInt() + positionOffsetPixels
                while (position < count && remainingWidth > 0) {
                    position += 1
                    remainingWidth -= (width * adapter.getPageWidth(position)).toInt()
                }
                position = count - position - 1
                positionOffsetPixels = -remainingWidth
                positionOffset = positionOffsetPixels / (width * adapter.getPageWidth(position))
            }
            listener.onPageScrolled(position, positionOffset, positionOffsetPixels)
        }

        override fun onPageSelected(pos: Int) {
            var position = pos
            val adapter: PagerAdapter? = super@RtlViewPager.getAdapter()
            if (isLayoutRtl() && adapter != null) {
                position = adapter.count - position - 1
            }
            listener.onPageSelected(position)
        }

        override fun onPageScrollStateChanged(state: Int) {
            listener.onPageScrollStateChanged(state)
        }
    }
}
