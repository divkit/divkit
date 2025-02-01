package com.yandex.div.core.view2.divs.pager

import com.yandex.div2.DivPager.ItemAlignment
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sign

internal class DivPagerPageOffsetProvider(
    private val itemSpacing: Float,
    private val pageSizeProvider: DivPagerPageSizeProvider,
    private val paddings: DivPagerPaddingsHolder,
    private val infiniteScroll: Boolean,
    private val adapter: DivPagerAdapter,
    private val alignment: ItemAlignment,
) {

    fun getPageOffset(position: Float, pagePosition: Int, isOverlap: Boolean) =
        getOffset(position, pagePosition) - getInitialOffset(position, pagePosition, isOverlap)

    private fun getOffset(position: Float, pagePosition: Int): Float {
        if (position == 0f) return 0f
        var pagePos = pagePosition
        var size = 0f
        val sign = position.sign.toInt()
        for (i in 1 .. abs(position).toInt()) {
            size += getOnePositionOffset(pagePos, sign)
            pagePos -= sign
        }
        return size + position.frac.let { if (it > 0) it * getOnePositionOffset(pagePos, sign) else 0f }
    }

    private fun getOnePositionOffset(position: Int, sign: Int): Float {
        val prev = pageSizeProvider.getPrevNeighbourSize(if (sign > 0) position else position + 1) ?: return 0f
        val next = pageSizeProvider.getNextNeighbourSize(if (sign > 0) position - 1 else position) ?: return 0f
        return (prev + next - itemSpacing) * sign
    }

    private fun getInitialOffset(position: Float, pagePosition: Int, isOverlap: Boolean): Float {
        if (isOverlap) return 0f
        if (infiniteScroll) return 0f
        getStartOffset(position, pagePosition).let { if (it != 0f) return it }
        getEndOffset(position, pagePosition).let { if (it != 0f) return it }
        return 0f
    }

    private fun getStartOffset(position: Float, pagePosition: Int): Float {
        if (alignment == ItemAlignment.START) return 0f

        val startOffset = pageSizeProvider.getPrevNeighbourSize(0)?.let { it - paddings.start }
        if (startOffset == null || startOffset == 0f) return 0f

        val activePage = pagePosition - ceil(position).toInt()
        val part = if (position <= 0) position.frac else position.fracInverted
        var prevItemsSize = pageSizeProvider.getItemSize(activePage)?.let { it * part } ?: return 0f
        if (prevItemsSize.biggerThan(startOffset)) return 0f

        for (i in activePage - 1 downTo 0) {
            prevItemsSize += pageSizeProvider.getItemSize(i)?.let { it + itemSpacing } ?: return 0f
            if (prevItemsSize.biggerThan(startOffset)) return 0f
        }

        return prevItemsSize - startOffset
    }

    private fun getEndOffset(position: Float, pagePosition: Int): Float {
        if (alignment == ItemAlignment.END) return 0f

        val endOffset = pageSizeProvider.getNextNeighbourSize(adapter.itemCount - 1)?.let { it - paddings.end }
        if (endOffset == null || endOffset == 0f) return 0f

        val activePage = pagePosition - floor(position).toInt()
        val part = if (position > 0) position.frac else position.fracInverted
        var nextItemsSize = pageSizeProvider.getItemSize(activePage)?.let { it * part } ?: return 0f
        if (nextItemsSize.biggerThan(endOffset)) return 0f

        for (i in activePage + 1 until adapter.itemCount) {
            nextItemsSize += pageSizeProvider.getItemSize(i)?.let { it + itemSpacing } ?: return 0f
            if (nextItemsSize.biggerThan(endOffset)) return 0f
        }

        return endOffset - nextItemsSize
    }

    private val Float.frac get() = abs(this).let { it - floor(it) }

    private val Float.fracInverted get() = frac.let { if (it > 0) 1 - it else 0f }

    private fun Float.biggerThan(maxOffset: Float) = this >= abs(maxOffset)
}
