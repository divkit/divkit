package com.yandex.div.core.state;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.yandex.div.core.view2.divs.gallery.DivGalleryItemHelper;

/**
 * Save recycler scroll position in {@link DivViewState}
 */
public class UpdateStateScrollListener extends RecyclerView.OnScrollListener {
    @NonNull
    private final DivViewState mDivViewState;
    @NonNull
    private final String mBlockId;
    @NonNull
    private final DivGalleryItemHelper mLayoutManager;

    public UpdateStateScrollListener(@NonNull String blockId, @NonNull DivViewState divViewState, @NonNull DivGalleryItemHelper layoutManager) {
        mDivViewState = divViewState;
        mBlockId = blockId;
        mLayoutManager = layoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        int visibleItemIndex = mLayoutManager.firstVisibleItemPosition();
        RecyclerView.ViewHolder visibleItemHolder = recyclerView.findViewHolderForLayoutPosition(visibleItemIndex);
        int scrollOffset = 0;
        if (visibleItemHolder != null) {
            if (mLayoutManager.getLayoutManagerOrientation() == RecyclerView.VERTICAL) {
                scrollOffset = visibleItemHolder.itemView.getTop() - mLayoutManager.getView().getPaddingTop();
            } else {
                scrollOffset = visibleItemHolder.itemView.getLeft() - mLayoutManager.getView().getPaddingLeft();
            }
        }
        mDivViewState.putBlockState(mBlockId, new GalleryState(visibleItemIndex, scrollOffset));
    }
}
