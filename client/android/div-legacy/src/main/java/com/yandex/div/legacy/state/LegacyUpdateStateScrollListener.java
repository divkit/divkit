package com.yandex.div.legacy.state;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LegacyUpdateStateScrollListener extends RecyclerView.OnScrollListener {
    @NonNull
    private final LegacyDivViewState mDivViewState;
    @NonNull
    private final String mBlockId;
    @NonNull
    private final LinearLayoutManager mLayoutManager;

    public LegacyUpdateStateScrollListener(
            @NonNull String blockId,
            @NonNull LegacyDivViewState divViewState,
            @NonNull LinearLayoutManager layoutManager
    ) {
        mDivViewState = divViewState;
        mBlockId = blockId;
        mLayoutManager = layoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        int visibleItemIndex = mLayoutManager.findFirstVisibleItemPosition();
        RecyclerView.ViewHolder visibleItemHolder = recyclerView.findViewHolderForLayoutPosition(visibleItemIndex);
        int scrollOffset = visibleItemHolder != null ? visibleItemHolder.itemView.getLeft() : 0;
        mDivViewState.putBlockState(mBlockId, new LegacyGalleryState(visibleItemIndex, scrollOffset));
    }
}
