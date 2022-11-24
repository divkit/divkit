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
        val visibleItemHolder = recyclerView.findViewHolderForLayoutPosition(visibleItemIndex)
        var scrollOffset = 0
        if (visibleItemHolder != null) {
            scrollOffset = if (layoutManager.getLayoutManagerOrientation() == RecyclerView.VERTICAL) {
                visibleItemHolder.itemView.top - layoutManager.view.paddingTop
            } else {
                visibleItemHolder.itemView.left - layoutManager.view.paddingLeft
            }
        }
        divViewState.putBlockState(blockId, GalleryState(visibleItemIndex, scrollOffset))
    }
}
