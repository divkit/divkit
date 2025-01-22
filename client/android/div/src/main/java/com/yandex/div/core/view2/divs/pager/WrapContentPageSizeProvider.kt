package com.yandex.div.core.view2.divs.pager

import androidx.recyclerview.widget.RecyclerView

internal class WrapContentPageSizeProvider(
    private val recyclerView: RecyclerView,
    private val isHorizontal: Boolean,
) : DivPagerPageSizeProvider {

    override fun getItemSize(position: Int): Float {
        return recyclerView.layoutManager?.findViewByPosition(position)?.let {
            (if (isHorizontal) it.width else it.height).toFloat()
        } ?: 0f
    }
}
