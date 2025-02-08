package com.yandex.div.core.view2.divs.pager

import android.view.View
import com.yandex.div.core.view2.divs.widgets.DivPagerView
import kotlin.math.max

internal class WrapContentPageSizeOffScreenPagesController(
    private val parent: DivPagerView,
    private val itemSpacing: Float,
    private val pageSizeProvider: DivPagerPageSizeProvider,
    private val paddings: DivPagerPaddingsHolder,
    private val adapter: DivPagerAdapter,
) {

    private var sidePagesCount = 1

    init {
        sidePagesCount = calcSidePagesCount()
        parent.setOffScreenPages()

        parent.changePageCallbackForOffScreenPages = object: DivPagerView.OffScreenPagesUpdateCallback() {

            override fun onPageSelected(position: Int) = updateOffScreenPages()

            override fun onLayoutChange(
                v: View?, left: Int, top: Int, right: Int, bottom: Int,
                oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int
            ) = updateOffScreenPages()
        }
    }

    private fun calcSidePagesCount(): Int {
        var prevSpace = pageSizeProvider.getPrevNeighbourSize(parent.currentItem) ?: return 1
        var countLeft = 0
        var prevPage = parent.currentItem - 1
        while (prevSpace > 0 && prevPage > 0) {
            countLeft++
            prevSpace -= pageSize(prevPage) ?: break
            prevPage--
        }
        if (prevSpace > paddings.start && prevPage == 0) {
            countLeft++
            prevSpace -= pageSize(prevPage) ?: 0f
        }

        var nextSpace = pageSizeProvider.getNextNeighbourSize(parent.currentItem) ?: return countLeft.coerceAtLeast(1)
        if (prevSpace > paddings.start) {
            nextSpace += prevSpace
        }
        var countRight = 0
        var nextPage = parent.currentItem + 1
        while (nextSpace > 0 && nextPage < adapter.itemCount - 1) {
            countRight++
            nextSpace -= pageSize(nextPage) ?: break
            nextPage++
        }
        if (nextSpace > paddings.end && nextPage == adapter.itemCount - 1) {
            countRight++
            nextSpace -= pageSize(nextPage) ?: 0f
        }

        while (nextSpace > 0 && prevPage >= 0) {
            countLeft++
            nextSpace -= pageSize(prevPage) ?: break
            prevPage--
        }

        return max(countLeft, countRight).coerceAtLeast(1)
    }

    private fun pageSize(page: Int) = pageSizeProvider.getItemSize(page)?.let { it + itemSpacing }

    private fun DivPagerView.setOffScreenPages() {
        getRecyclerView()?.setItemViewCacheSize(sidePagesCount * 2 + 3)
        viewPager.offscreenPageLimit = sidePagesCount
    }

    private fun updateOffScreenPages() {
        val count = calcSidePagesCount()
        if (count <= sidePagesCount)  return

        sidePagesCount = count
        parent.setOffScreenPages()
    }
}
