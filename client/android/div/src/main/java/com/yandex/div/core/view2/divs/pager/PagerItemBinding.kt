package com.yandex.div.core.view2.divs.pager

import android.util.SparseArray
import androidx.viewpager2.widget.ViewPager2
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.DivViewCreator
import com.yandex.div.core.view2.divs.CollectionPrebinding
import com.yandex.div.core.view2.divs.DefaultCollectionItemBinding
import com.yandex.div.core.view2.divs.widgets.DivPagerView
import com.yandex.div.internal.core.DivBlock

internal class PagerItemBinding(
    items: List<DivBlock>,
    private val pagerView: DivPagerView,
    divView: Div2View,
    divBinder: DivBinder,
    viewCreator: DivViewCreator,
    pageTranslations: SparseArray<Float>,
) {
    val adapter: DivPagerAdapter
    private val prebinding: CollectionPrebinding<DivPagerViewHolder>

    init {
        lateinit var createdAdapter: DivPagerAdapter
        val isHorizontal = { pagerView.orientation == ViewPager2.ORIENTATION_HORIZONTAL }
        val binding = DefaultCollectionItemBinding(
            createHolder = {
                DivPagerViewHolder(
                    DivPagerPageLayout(divView.context, isHorizontal),
                    divBinder,
                    viewCreator,
                    divView,
                    isHorizontal,
                ) { pagerView.crossAxisAlignment }
            },
            bindHolder = { holder, block, position -> holder.bind(block, createdAdapter.realItemPosition(position)) },
        )
        prebinding = CollectionPrebinding(
            binding = binding,
            divView = divView,
            isAdapterCurrent = { pagerView.viewPager.adapter === createdAdapter },
            itemAt = { createdAdapter.itemsToShow.getOrNull(it) },
            nativeViewTypes = { (0 until createdAdapter.itemCount).map(createdAdapter::getItemViewType).toSet() },
            onPreparedItemConsumed = { holder, block -> holder.updatePageLayout(block) },
        )
        createdAdapter = DivPagerAdapter(items, prebinding, pageTranslations, pagerView)
        adapter = createdAdapter
    }

    fun prepare() {
        val parent = pagerView.getRecyclerView() ?: return
        val current = pagerView.currentItem
        prebinding.prepare(parent, listOf(current, current - 1, current + 1))
    }
}
