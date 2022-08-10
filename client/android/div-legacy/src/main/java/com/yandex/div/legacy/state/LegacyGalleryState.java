package com.yandex.div.legacy.state;

public class LegacyGalleryState implements LegacyDivViewState.BlockState {
    private final int mVisibleItemIndex;
    private final int mScrollOffset;

    public LegacyGalleryState(int visibleItemIndex, int scrollOffset) {
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
