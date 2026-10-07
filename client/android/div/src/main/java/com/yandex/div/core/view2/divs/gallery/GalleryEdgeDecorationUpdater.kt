package com.yandex.div.core.view2.divs.gallery

/**
 * Single-column galleries pad their first and last items differently. Rebind the adjacent item
 * after an edge insertion or removal so RecyclerView recalculates its decoration offsets.
 */
internal class GalleryEdgeDecorationUpdater(private val notifyItemChanged: (Int) -> Unit) {
    fun onRemoved(position: Int, itemCount: Int, columnCount: Int) {
        if (columnCount != 1) return
        when {
            position == 0 && itemCount > 0 -> notifyItemChanged(0)
            position == itemCount -> notifyItemChanged(position - 1)
        }
    }

    fun onInserted(position: Int, count: Int, itemCount: Int, columnCount: Int) {
        if (columnCount != 1) return
        when {
            position == 0 && itemCount > count -> notifyItemChanged(count)
            position + count == itemCount && position > 0 -> notifyItemChanged(position - 1)
        }
    }
}
