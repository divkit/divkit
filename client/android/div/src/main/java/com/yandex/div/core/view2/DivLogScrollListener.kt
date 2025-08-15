package com.yandex.div.core.view2

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

internal class DivLogScrollListener(
    private val layoutManager: LinearLayoutManager,
    private val isVertical: Boolean,
    private val scrollGap: Int,
    private val listener: OnViewHolderVisibleListener
) : RecyclerView.OnScrollListener() {

    private var totalScroll = 0

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        totalScroll += Math.abs(if (isVertical) dy else dx)
        val size = if (isVertical) layoutManager.height else layoutManager.width
        val minimumScroll = size / scrollGap

        if (totalScroll > minimumScroll) {
            totalScroll = 0
            val first = layoutManager.findFirstVisibleItemPosition()
            val last = layoutManager.findLastVisibleItemPosition()
            for (i in first until last) {
                listener.onViewHolderVisible(i)
            }
        }
    }
}

internal interface OnViewHolderVisibleListener {
    fun onViewHolderVisible(position: Int)
}
