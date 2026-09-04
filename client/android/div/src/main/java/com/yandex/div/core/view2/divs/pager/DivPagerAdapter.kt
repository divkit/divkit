package com.yandex.div.core.view2.divs.pager

import android.util.SparseArray
import android.view.ViewGroup
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
        override val size get() = visibleItems.size + if (infiniteScrollEnabled) OFFSET_TO_REAL_ITEM * 2 else 0

        override fun get(index: Int): DivBlock {
            if (!infiniteScrollEnabled) return visibleItems[index]

            return visibleItems[realItemPosition(index)]
        }
    }

    private val offsetToRealItem get() = if (infiniteScrollEnabled) OFFSET_TO_REAL_ITEM else 0

    fun realItemPosition(position: Int): Int {
        return normalizeItemPosition(getRealPosition(position))
    }

    fun normalizeItemPosition(position: Int): Int {
        val size = visibleItems.size.takeIf { it > 0 } ?: return 0
        return (position + size) % size
    }

    var infiniteScrollEnabled = false
        set(value) {
            if (field == value) return
            field = value
            notifyItemRangeChanged(0, itemCount)
            pagerView.currentItem += if (value) OFFSET_TO_REAL_ITEM else -OFFSET_TO_REAL_ITEM
        }

    private var removedItems = 0

    fun getPosition(visibleItemIndex: Int) = visibleItemIndex + offsetToRealItem

    fun getRealPosition(rawPosition: Int) = rawPosition - offsetToRealItem

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
        if (!infiniteScrollEnabled) {
            notifyItemRemoved(position)
            return
        }

        notifyItemRemoved(position + OFFSET_TO_REAL_ITEM)
        notifyVirtualItemsChanged(position)
    }

    override fun notifyRawItemInserted(position: Int) {
        if (!infiniteScrollEnabled) {
            notifyItemInserted(position)
            return
        }

        notifyItemInserted(position + OFFSET_TO_REAL_ITEM)
        notifyVirtualItemsChanged(position)
    }

    override fun notifyRawItemChanged(position: Int) {
        if (!infiniteScrollEnabled) {
            notifyItemChanged(position)
            return
        }

        notifyItemChanged(position + OFFSET_TO_REAL_ITEM)
        notifyVirtualItemsChanged(position)
    }

    private fun notifyVirtualItemsChanged(originalPosition: Int) {
        when (originalPosition) {
            in 0 until OFFSET_TO_REAL_ITEM -> {
                notifyItemRangeChanged(
                    visibleItems.size + originalPosition,
                    OFFSET_TO_REAL_ITEM - originalPosition
                )
            }
            in visibleItems.size - OFFSET_TO_REAL_ITEM  until visibleItems.size -> {
                notifyItemRangeChanged(
                    originalPosition - visibleItems.size + OFFSET_TO_REAL_ITEM,
                    OFFSET_TO_REAL_ITEM
                )
            }
        }
    }

    companion object {
        const val OFFSET_TO_REAL_ITEM = 2
    }
}
