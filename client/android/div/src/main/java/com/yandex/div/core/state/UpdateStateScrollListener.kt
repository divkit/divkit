package com.yandex.div.core.state

import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.core.view2.divs.gallery.DivGalleryItemHelper

/**
 * Save recycler scroll position in [DivViewState]
 */
internal class UpdateStateScrollListener(
    private val blockId: String,
    private val divViewState: DivViewState,
    private val layoutManager: DivGalleryItemHelper
) : RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val visibleItemIndex = layoutManager.firstVisibleItemPosition()
        val scrollOffset = recyclerView.findViewHolderForLayoutPosition(visibleItemIndex)?.itemView?.let {
            layoutManager.calcScrollOffset(it)
        } ?: 0
        divViewState.putBlockState(blockId, GalleryState(visibleItemIndex, scrollOffset))
    }
}
