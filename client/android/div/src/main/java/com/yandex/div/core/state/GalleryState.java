package com.yandex.div.core.state;

public class GalleryState implements DivViewState.BlockState {
    private final int mVisibleItemIndex;
    private final int mScrollOffset;

    public GalleryState(int visibleItemIndex, int scrollOffset) {
        mVisibleItemIndex = visibleItemIndex;
        mScrollOffset = scrollOffset;
    }

    public int getVisibleItemIndex() {
        return mVisibleItemIndex;
    }

    public int getScrollOffset() {
        return mScrollOffset;
    }
}
