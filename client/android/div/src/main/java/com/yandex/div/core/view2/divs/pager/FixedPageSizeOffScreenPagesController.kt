package com.yandex.div.core.view2.divs.pager

import androidx.viewpager2.widget.ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT
import com.yandex.div.core.util.ViewProperty
import com.yandex.div.core.util.doOnActualLayout
import com.yandex.div.core.view2.divs.widgets.DivPagerView
import kotlin.math.ceil
import kotlin.math.max

internal class FixedPageSizeOffScreenPagesController(
    private val parent: DivPagerView,
    private val parentSize: ViewProperty<Int>,
    private val itemSpacing: Float,
    private val pageSizeProvider: FixedPageSizeProvider,
    private val paddings: DivPagerPaddingsHolder,
    private val infiniteScroll: Boolean,
    private val adapter: DivPagerAdapter,
) {

    init {
        parent.doOnActualLayout { setOffScreenPages() }
    }

    private fun setOffScreenPages() {
        if (pageSizeProvider.itemSize == 0f) return

        val pager = parent.viewPager
        val onScreenPages = parentSize.get() / (pageSizeProvider.itemSize + itemSpacing)
        parent.getRecyclerView()?.setItemViewCacheSize(ceil(onScreenPages).toInt() + 2)

        if (pageSizeProvider.hasOffScreenPages) {
            pager.offscreenPageLimit = max(ceil(onScreenPages - 1).toInt(), 1)
            return
        }

        val neighbourSize = pageSizeProvider.neighbourSize
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

        parent.changePageCallbackForOffScreenPages = object: DivPagerView.OffScreenPagesUpdateCallback() {
            override fun onPageSelected(position: Int) = setOffScreenPages(position)
        }
    }
}
