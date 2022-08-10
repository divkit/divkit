package com.yandex.div.legacy.state;

public class LegacyTabsState implements LegacyDivViewState.BlockState {
    private final int mCurrentPage;

    public LegacyTabsState(int currentPage) {
        this.mCurrentPage = currentPage;
    }

    public int getCurrentPage() {
        return mCurrentPage;
    }
}
