package com.yandex.div.core.view2.divs.pager

import android.util.SparseArray
import android.view.ViewGroup
import androidx.annotation.IntRange
import androidx.viewpager2.widget.ViewPager2
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.DivViewCreator
import com.yandex.div.core.view2.divs.DivCollectionAdapter
import com.yandex.div.core.view2.divs.widgets.DivPagerView
import com.yandex.div.internal.core.DivBlock

internal class DivPagerAdapter(
    items: List<DivBlock>,
    private val divView: Div2View,
    private val divBinder: DivBinder,
    private val pageTranslations: SparseArray<Float>,
    private val viewCreator: DivViewCreator,
    private val pagerView: DivPagerView,
) : DivCollectionAdapter<DivPagerViewHolder>(items) {

    val itemsToShow = object : AbstractList<DivBlock>() {
        override val size get() = visibleItems.size + virtualItemCount * 2

        override fun get(index: Int): DivBlock =
            if (virtualItemCount == 0) visibleItems[index] else visibleItems[realItemPosition(index)]
    }

    var requestedVirtualItemCount = 0
        set(@IntRange(from = 0) value) {
            if (field == value) return

            field = value
            val offset = updateVirtualItemCount()
            if (offset == 0) return

            notifyVirtualItemCountChanged(offset)
        }

    var virtualItemCount = 0
        private set

    private fun updateVirtualItemCount(): Int {
        val prevVirtualItemCount = virtualItemCount
        virtualItemCount = if (visibleItems.size > 1) requestedVirtualItemCount else 0
        return virtualItemCount - prevVirtualItemCount
    }

    fun realItemPosition(position: Int): Int {
        return normalizeItemPosition(getRealPosition(position))
    }

    fun normalizeItemPosition(position: Int): Int {
        val size = visibleItems.size.takeIf { it > 0 } ?: return 0
        return position.mod(size)
    }

    fun getPosition(visibleItemIndex: Int) = visibleItemIndex + virtualItemCount

    fun getRealPosition(rawPosition: Int) = rawPosition - virtualItemCount

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DivPagerViewHolder {
        val orientationProvider = { pagerView.orientation == ViewPager2.ORIENTATION_HORIZONTAL }
        val view = DivPagerPageLayout(divView.context, orientationProvider)
        return DivPagerViewHolder(view, divBinder, viewCreator, divView, orientationProvider) {
            pagerView.crossAxisAlignment
        }
    }

    override fun getItemCount() = itemsToShow.size

    override fun onBindViewHolder(holder: DivPagerViewHolder, position: Int) {
        super.onBindViewHolder(holder, realItemPosition(position))
        pageTranslations[position]?.let { holder.applyTranslation(it) }
    }

    private var removedItems = 0

    override fun setItems(newItems: List<DivBlock>) {
        val oldSize = items.size
        removedItems = 0
        val oldCurrentItem = pagerView.currentItem
        super.setItems(newItems)
        if (removedItems == oldSize) {
            pagerView.currentItem =  oldCurrentItem
        }
    }

    override fun notifyRawItemRemoved(position: Int) {
        removedItems++
        val offset = -updateVirtualItemCount()

        if (offset == 0) {
            notifyItemRemoved(position + virtualItemCount)
            notifyVirtualItemsChanged()
            return
        }

        notifyItemRangeRemoved(visibleItems.size + offset + 1, offset)
        notifyItemRemoved(position + offset)
        notifyItemRangeRemoved(0, offset)

        pagerView.currentItem -= offset
    }

    override fun notifyRawItemsInserted(position: Int, count: Int) {
        val offset = updateVirtualItemCount()

        if (offset == 0) {
            notifyItemRangeInserted(position + virtualItemCount, count)
            notifyVirtualItemsChanged()
            return
        }

        if (visibleItems.size - count == 0) {
            notifyItemRangeInserted(0, itemCount)
        } else {
            notifyItemRangeInserted(0, offset)
            notifyItemRangeInserted(position + virtualItemCount, count)
            notifyItemRangeInserted(itemCount - offset, offset)
        }

        pagerView.currentItem += offset
        return
    }

    override fun notifyRawItemChanged(position: Int) {
        notifyItemChanged(position + virtualItemCount)
        notifyVirtualItemsChanged()
    }

    private fun notifyVirtualItemCountChanged(itemOffset: Int) {
        if (itemOffset > 0) {
            notifyItemRangeInserted(0, itemOffset)
            notifyItemRangeInserted(itemCount - itemOffset, itemOffset)
        } else {
            notifyItemRangeRemoved(0, -itemOffset)
            notifyItemRangeRemoved(itemCount, -itemOffset)
        }
        pagerView.currentItem += itemOffset
    }

    private fun notifyVirtualItemsChanged() {
        if (virtualItemCount == 0) return

        notifyItemRangeChanged(0, virtualItemCount)
        notifyItemRangeChanged(getPosition(visibleItems.size), virtualItemCount)
    }
}
