package com.yandex.div.core.view2.divs.pager

import com.yandex.div2.DivPager.ItemAlignment

internal abstract class DivPagerPageSizeProvider(
    private val parentSize: Int,
    private val paddings: DivPagerPaddingsHolder,
    private val alignment: ItemAlignment,
) {

    abstract fun getItemSize(position: Int): Float?

    fun getPrevNeighbourSize(position: Int): Float? = when (alignment) {
        ItemAlignment.START -> paddings.start
        ItemAlignment.CENTER -> getCenteredNeighbourSize(position)
        ItemAlignment.END -> getItemSize(position)?.let { parentSize - paddings.end - it }
    }

    fun getNextNeighbourSize(position: Int) = when (alignment) {
        ItemAlignment.START -> getItemSize(position)?.let { parentSize - paddings.start - it }
        ItemAlignment.CENTER -> getCenteredNeighbourSize(position)
        ItemAlignment.END -> paddings.end
    }

    private fun getCenteredNeighbourSize(position: Int) = getItemSize(position)?.let { (parentSize - it) / 2f }
}
