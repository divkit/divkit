package com.yandex.div.core.view2.divs.pager

import android.util.SparseArray
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.viewpager2.widget.ViewPager2
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.DivViewCreator
import com.yandex.div.core.view2.divs.DivCollectionAdapter
import com.yandex.div.internal.core.DivItemBuilderResult

internal class DivPagerAdapter(
    items: List<DivItemBuilderResult>,
    private val bindingContext: BindingContext,
    private val divBinder: DivBinder,
    private val pageTranslations: SparseArray<Float>,
    private val viewCreator: DivViewCreator,
    private val path: DivStatePath,
    private val accessibilityEnabled: Boolean,
) : DivCollectionAdapter<DivPagerViewHolder>(items) {

    val itemsToShow = object : AbstractList<DivItemBuilderResult>() {
        override val size get() = visibleItems.size + if (infiniteScrollEnabled) OFFSET_TO_REAL_ITEM * 2 else 0

        override fun get(index: Int): DivItemBuilderResult {
            if (!infiniteScrollEnabled) return visibleItems[index]

            val position = (visibleItems.size + index - OFFSET_TO_REAL_ITEM).mod(visibleItems.size)
            return visibleItems[position]
        }
    }

    var orientation = ViewPager2.ORIENTATION_HORIZONTAL

    var infiniteScrollEnabled = false
        set(value) {
            if (field == value) return
            field = value
            notifyItemRangeChanged(0, itemCount)
        }

    fun getPosition(visibleItemIndex: Int) = visibleItemIndex + if (infiniteScrollEnabled) OFFSET_TO_REAL_ITEM else 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DivPagerViewHolder {
        val view = DivPagerPageLayout(bindingContext.divView.context) { orientation }
        view.layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)

        return DivPagerViewHolder(bindingContext, view, divBinder, viewCreator, path, accessibilityEnabled)
    }

    override fun getItemCount() = itemsToShow.size

    override fun onBindViewHolder(holder: DivPagerViewHolder, position: Int) {
        val item = itemsToShow[position]
        holder.bind(bindingContext.getFor(item.expressionResolver), item.div, position)
        pageTranslations[position]?.let {
            if (orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
                holder.itemView.translationX = it
            } else {
                holder.itemView.translationY = it
            }
        }
    }

    override fun notifyRawItemRemoved(position: Int) {
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

    override fun notifyRawItemRangeInserted(positionStart: Int, itemCount: Int) {
        if (!infiniteScrollEnabled) {
            notifyItemRangeInserted(positionStart, itemCount)
            return
        }

        notifyItemRangeInserted(positionStart + OFFSET_TO_REAL_ITEM, itemCount)
        notifyVirtualItemsChanged(positionStart)
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
