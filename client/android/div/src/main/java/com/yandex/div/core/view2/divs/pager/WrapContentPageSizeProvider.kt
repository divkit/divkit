package com.yandex.div.core.view2.divs.pager

import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.core.util.ViewProperty
import com.yandex.div2.DivPager

internal class WrapContentPageSizeProvider(
    private val recyclerView: RecyclerView,
    private val isHorizontal: Boolean,
    parentSize: ViewProperty<Int>,
    paddings: DivPagerPaddingsHolder,
    alignment: DivPager.ItemAlignment,
) : DivPagerPageSizeProvider(parentSize, paddings, alignment) {

    override fun getItemSize(position: Int): Float? {
        return recyclerView.layoutManager?.findViewByPosition(position)?.let {
            (if (isHorizontal) it.width else it.height).toFloat()
        }
    }
}
