package com.yandex.div.view.tabs;

import android.os.Parcelable;
import android.util.SparseArray;
import androidx.annotation.Dimension;
import androidx.annotation.NonNull;

/**
 * Noop calculator with fixed tab height
 */
public class FixedHeightCalculator implements ViewPagerFixedSizeLayout.HeightCalculator {

    @Dimension
    private final int mTabHeight;

    public FixedHeightCalculator(@Dimension int height) {
        mTabHeight = height;
    }

    @Override
    public void setPositionAndOffsetForMeasure(int position, float positionOffset) { /* not used */ }

    @Override
    public int measureHeight(int widthMeasureSpec, int heightMeasureSpec) {
        return mTabHeight;
    }

    @Override
    public boolean shouldRequestLayoutOnScroll(int position, float positionOffset) {
        return false;
    }

    @Override
    public void dropMeasureCache() { /* not used */ }

    @Override
    public void saveInstanceState(@NonNull SparseArray<Parcelable> container) { /* not used */ }

    @Override
    public void restoreInstanceState(@NonNull SparseArray<Parcelable> container) { /* not used */ }
}
