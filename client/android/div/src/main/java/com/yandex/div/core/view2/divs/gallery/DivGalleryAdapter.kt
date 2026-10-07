package com.yandex.div.core.view2.divs.gallery

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.core.view2.divs.CollectionItemBinding
import com.yandex.div.core.view2.divs.DivCollectionAdapter
import com.yandex.div.internal.core.DivBlock

internal class DivGalleryAdapter(
    items: List<DivBlock>,
    private val itemBinding: CollectionItemBinding<DivGalleryViewHolder>,
) : DivCollectionAdapter<DivGalleryViewHolder>(items) {

    var orientation = RecyclerView.HORIZONTAL
    var columnCount = 1
    var crossSpacing = 0f

    private val internalIds = GalleryItemIds()
    private val edgeDecorations = GalleryEdgeDecorationUpdater(::notifyRawItemChanged)

    init {
        setHasStableIds(true)
    }

    override fun getItemViewType(position: Int): Int =
        itemBinding.viewType(position, super.getItemViewType(position))

    fun releasePreparedItems() = itemBinding.clear()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DivGalleryViewHolder =
        itemBinding.create(parent, viewType)

    override fun onBindViewHolder(holder: DivGalleryViewHolder, position: Int) {
        itemBinding.bind(holder, visibleItems[position], position)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        itemBinding.clear()
        super.onDetachedFromRecyclerView(recyclerView)
    }

    override fun setItems(newItems: List<DivBlock>) {
        itemBinding.clear()
        super.setItems(newItems)
    }

    override fun notifyRawItemChanged(position: Int) {
        itemBinding.clear()
        super.notifyRawItemChanged(position)
    }

    override fun getItemId(position: Int): Long = internalIds.get(visibleItems[position])

    override fun notifyRawItemRemoved(position: Int) {
        itemBinding.clear()
        notifyItemRemoved(position)
        edgeDecorations.onRemoved(position = position, itemCount = itemCount, columnCount = columnCount)
    }

    override fun notifyRawItemsInserted(position: Int, count: Int) {
        itemBinding.clear()
        notifyItemRangeInserted(position, count)
        edgeDecorations.onInserted(position = position, count = count, itemCount = itemCount, columnCount = columnCount)
    }
}
