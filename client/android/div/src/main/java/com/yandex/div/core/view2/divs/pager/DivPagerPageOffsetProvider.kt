package com.yandex.div.core.view2.divs.pager

import androidx.viewpager2.widget.ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.yandex.div.core.view2.divs.widgets.DivPagerView
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max

internal class DivPagerPageOffsetProvider(
    private val parent: DivPagerView,
    private val parentSize: Int,
    private val itemSpacing: Float,
    private val pageSizeProvider: DivPagerPageSizeProvider,
    private val paddings: DivPagerPaddingsHolder,
    private val infiniteScroll: Boolean,
    private val adapter: DivPagerAdapter,
) {

    init {
        setOffScreenPages()
    }

    private fun setOffScreenPages() {
        if (pageSizeProvider.itemSize == 0f) return

        val pager = parent.viewPager
        val onScreenPages = parentSize / (pageSizeProvider.itemSize + itemSpacing)
        parent.getRecyclerView()?.setItemViewCacheSize(ceil(onScreenPages).toInt() + 2)

        if (pageSizeProvider.hasOffScreenPages) {
            pager.offscreenPageLimit = max(ceil(onScreenPages - 1).toInt(), 1)
            return
        }

        if (neighbourSize > itemSpacing) {
            pager.offscreenPageLimit = 1
            return
        }

        val showNeighbourForSidePages =
            !infiniteScroll && (paddings.start < neighbourSize || paddings.end < neighbourSize)

        if (!showNeighbourForSidePages) {
            pager.offscreenPageLimit = OFFSCREEN_PAGE_LIMIT_DEFAULT
            return
        }

        val setOffScreenPages = { position: Int ->
            parent.viewPager.offscreenPageLimit =
                if (position == 0 || position == adapter.itemCount - 1) 1 else OFFSCREEN_PAGE_LIMIT_DEFAULT
        }

        setOffScreenPages(pager.currentItem)

        pager.registerOnPageChangeCallback(object: OnPageChangeCallback() {
            override fun onPageSelected(position: Int) = setOffScreenPages(position)
        })
    }

    private val neighbourSize get() = pageSizeProvider.neighbourSize

    fun getPageOffset(position: Float, pagePosition: Int, isOverlap: Boolean) =
        position * (neighbourSize * 2 - itemSpacing) - getInitialOffset(position, pagePosition, isOverlap)

    private fun getInitialOffset(position: Float, pagePosition: Int, isOverlap: Boolean): Float {
        if (isOverlap) return 0f
        if (infiniteScroll) return 0f
        getStartOffset(position, pagePosition).let { if (it != 0f) return it }
        getEndOffset(position, pagePosition).let { if (it != 0f) return it }
        return 0f
    }

    private fun getStartOffset(position: Float, pagePosition: Int): Float {
        val startOffset = neighbourSize - paddings.start
        if (startOffset == 0f) return 0f

        val activePage = pagePosition - ceil(position).toInt()
        val part = if (position <= 0) position.frac else position.fracInverted
        var prevItemsSize = pageSizeProvider.itemSize * part
        if (prevItemsSize.biggerThan(startOffset)) return 0f

        for (i in activePage - 1 downTo 0) {
            prevItemsSize += pageSizeProvider.itemSize + itemSpacing
            if (prevItemsSize.biggerThan(startOffset)) return 0f
        }

        return prevItemsSize - startOffset
    }

    private fun getEndOffset(position: Float, pagePosition: Int): Float {
        val endOffset = neighbourSize - paddings.end
        if (endOffset == 0f) return 0f

        val activePage = pagePosition - floor(position).toInt()
        val part = if (position > 0) position.frac else position.fracInverted
        var nextItemsSize = pageSizeProvider.itemSize * part
        if (nextItemsSize.biggerThan(endOffset)) return 0f

        for (i in activePage + 1 until adapter.itemCount) {
            nextItemsSize += pageSizeProvider.itemSize + itemSpacing
            if (nextItemsSize.biggerThan(endOffset)) return 0f
        }

        return endOffset - nextItemsSize
    }

    private val Float.frac get() = abs(this).let { it - floor(it) }

    private val Float.fracInverted get() = frac.let { if (it > 0) 1 - it else 0f }

    private fun Float.biggerThan(maxOffset: Float) = this >= abs(maxOffset)
}
