package com.yandex.div.core.view2.divs.pager

import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sign

internal class DivPagerPageOffsetProvider(
    private val parentSize: Int,
    private val itemSpacing: Float,
    private val pageSizeProvider: DivPagerPageSizeProvider,
    private val paddings: DivPagerPaddingsHolder,
    private val infiniteScroll: Boolean,
    private val adapter: DivPagerAdapter,
) {

    fun getPageOffset(position: Float, pagePosition: Int, isOverlap: Boolean) =
        getOffset(position, pagePosition) - getInitialOffset(position, pagePosition, isOverlap)

    private fun getOffset(position: Float, pagePosition: Int): Float {
        var pagePos = pagePosition
        var size = 0f
        val sign = position.sign.toInt()
        val getOnePositionOffset = { pos: Int ->
            sign * (parentSize - itemSpacing -
                (pageSizeProvider.getItemSize(pos) + pageSizeProvider.getItemSize(pos - sign)) / 2f)
        }
        for (i in 1 .. abs(position).toInt()) {
            size += getOnePositionOffset(pagePos)
            pagePos -= sign
        }
        return size + position.frac * getOnePositionOffset(pagePos)
    }

    private fun getInitialOffset(position: Float, pagePosition: Int, isOverlap: Boolean): Float {
        if (isOverlap) return 0f
        if (infiniteScroll) return 0f
        getStartOffset(position, pagePosition).let { if (it != 0f) return it }
        getEndOffset(position, pagePosition).let { if (it != 0f) return it }
        return 0f
    }

    private fun getStartOffset(position: Float, pagePosition: Int): Float {
        val startOffset = getNeighbourSize(0) - paddings.start
        if (startOffset == 0f) return 0f

        val activePage = pagePosition - ceil(position).toInt()
        val part = if (position <= 0) position.frac else position.fracInverted
        var prevItemsSize = pageSizeProvider.getItemSize(activePage) * part
        if (prevItemsSize.biggerThan(startOffset)) return 0f

        for (i in activePage - 1 downTo 0) {
            prevItemsSize += pageSizeProvider.getItemSize(i) + itemSpacing
            if (prevItemsSize.biggerThan(startOffset)) return 0f
        }

        return prevItemsSize - startOffset
    }

    private fun getEndOffset(position: Float, pagePosition: Int): Float {
        val endOffset = getNeighbourSize(adapter.itemCount - 1) - paddings.end
        if (endOffset == 0f) return 0f

        val activePage = pagePosition - floor(position).toInt()
        val part = if (position > 0) position.frac else position.fracInverted
        var nextItemsSize = pageSizeProvider.getItemSize(activePage) * part
        if (nextItemsSize.biggerThan(endOffset)) return 0f

        for (i in activePage + 1 until adapter.itemCount) {
            nextItemsSize += pageSizeProvider.getItemSize(i) + itemSpacing
            if (nextItemsSize.biggerThan(endOffset)) return 0f
        }

        return endOffset - nextItemsSize
    }

    private fun getNeighbourSize(position: Int) = (parentSize - pageSizeProvider.getItemSize(position)) / 2f

    private val Float.frac get() = abs(this).let { it - floor(it) }

    private val Float.fracInverted get() = frac.let { if (it > 0) 1 - it else 0f }

    private fun Float.biggerThan(maxOffset: Float) = this >= abs(maxOffset)
}
