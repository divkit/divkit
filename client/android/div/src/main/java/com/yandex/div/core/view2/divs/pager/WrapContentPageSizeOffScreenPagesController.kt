package com.yandex.div.core.view2.divs.pager

import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.yandex.div.core.view2.divs.widgets.DivPagerView
import kotlin.math.max

internal class WrapContentPageSizeOffScreenPagesController(
    private val parent: DivPagerView,
    private val parentSize: Int,
    private val itemSpacing: Float,
    private val pageSizeProvider: DivPagerPageSizeProvider,
    private val paddings: DivPagerPaddingsHolder,
    private val adapter: DivPagerAdapter,
) {

    private var sidePagesCount = 1

    init {
        sidePagesCount = calcSidePagesCount()
        parent.setOffScreenPages()

        parent.changePageCallbackForOffScreenPages = object: OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val count = calcSidePagesCount()
                if (count <= sidePagesCount)  return

                sidePagesCount = count
                parent.setOffScreenPages()
            }
        }
    }

    private fun calcSidePagesCount(): Int {
        var countLeft = 0
        var leftSpace = parentSize - pageSize(parent.currentItem) / 2f
        var rightSpace = leftSpace
        var page = parent.currentItem - 1
        while (leftSpace > 0 && page > 0) {
            leftSpace -= pageSize(page)
            countLeft++
            page--
        }
        if (leftSpace > paddings.left && page == 0) {
            countLeft++
        }

        var countRight = 0
        page = parent.currentItem + 1
        while (rightSpace > 0 && page < adapter.itemCount - 1) {
            rightSpace -= pageSize(page)
            countRight++
            page++
        }
        if (rightSpace > paddings.right && page == adapter.itemCount - 1) {
            countRight++
        }

        return max(countLeft, countRight)
    }

    private fun pageSize(page: Int) = pageSizeProvider.getItemSize(page) + itemSpacing

    private fun DivPagerView.setOffScreenPages() {
        getRecyclerView()?.setItemViewCacheSize(sidePagesCount * 2 + 3)
        viewPager.offscreenPageLimit = sidePagesCount
    }
}
