package com.yandex.div.core.view2.divs.pager

import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.core.util.isLayoutRtl
import com.yandex.div2.DivPager

internal class WrapContentPageSizeProvider(
    private val recyclerView: RecyclerView,
    private val isHorizontal: Boolean,
    parentSize: Int,
    paddings: DivPagerPaddingsHolder,
    alignment: DivPager.ItemAlignment,
) : DivPagerPageSizeProvider(parentSize, paddings, alignment) {

    override fun getItemSize(position: Int): Float? {
        return recyclerView.layoutManager?.findViewByPosition(position)?.let {
            (if (isHorizontal) it.width else it.height).toFloat()
        }
    }

    override fun getCenteredPrevNeighbourSize(position: Int) =
        getDecorationOffset(position, isStart = true)

    override fun getCenteredNextNeighbourSize(position: Int) =
        getDecorationOffset(position, isStart = false)

    private fun getDecorationOffset(position: Int, isStart: Boolean): Float? {
        val layoutManager = recyclerView.layoutManager ?: return null
        val page = layoutManager.findViewByPosition(position) ?: return null
        val isLeftOrTop = isStart != (isHorizontal && recyclerView.isLayoutRtl())
        return when {
            isHorizontal && isLeftOrTop -> layoutManager.getLeftDecorationWidth(page)
            isHorizontal -> layoutManager.getRightDecorationWidth(page)
            isLeftOrTop -> layoutManager.getTopDecorationHeight(page)
            else -> layoutManager.getBottomDecorationHeight(page)
        }.toFloat()
    }
}
