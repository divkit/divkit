package com.yandex.div.core.view2.divs.gallery

import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.DivViewCreator
import com.yandex.div.core.view2.divs.CollectionPrebinding
import com.yandex.div.core.view2.divs.DefaultCollectionItemBinding
import com.yandex.div.core.view2.divs.widgets.DivRecyclerView
import com.yandex.div.internal.core.DivBlock

internal class GalleryItemBinding(
    items: List<DivBlock>,
    private val galleryView: DivRecyclerView,
    divView: Div2View,
    divBinder: DivBinder,
    viewCreator: DivViewCreator,
) {
    val adapter: DivGalleryAdapter
    private val prebinding: CollectionPrebinding<DivGalleryViewHolder>

    init {
        lateinit var createdAdapter: DivGalleryAdapter
        val binding = DefaultCollectionItemBinding<DivGalleryViewHolder>(
            createHolder = { DivGalleryViewHolder.create(createdAdapter, divView, divBinder, viewCreator) },
        )
        prebinding = CollectionPrebinding(
            binding = binding,
            divView = divView,
            isAdapterCurrent = { galleryView.adapter === createdAdapter },
            itemAt = { createdAdapter.visibleItems.getOrNull(it) },
            nativeViewTypes = { (0 until createdAdapter.itemCount).map(createdAdapter::getItemViewType).toSet() },
        )
        createdAdapter = DivGalleryAdapter(items, prebinding)
        adapter = createdAdapter
    }

    fun prepare(firstPosition: Int) {
        if (adapter.itemCount == 0) return
        val first = firstPosition.coerceIn(0, adapter.itemCount - 1)
        prebinding.prepare(galleryView, first until minOf(first + INITIAL_PREPARED_ITEMS, adapter.itemCount))
    }

    private companion object {
        const val INITIAL_PREPARED_ITEMS = 3
    }
}
