package com.yandex.div.internal.widget.tabs;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.CallSuper;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yandex.div.R;
import com.yandex.div.internal.Log;

@MainThread
public abstract class BaseCardHeightCalculator implements ViewPagerFixedSizeLayout.HeightCalculator {

    private static final String TAG = "[Y:BaseCardHeightCalculator]";

    private static final String KEY_FONT_SCALE = "FONT_SCALE";

    @NonNull
    private final ViewGroup mChannelGroup;

    @NonNull
    private final HeightCalculatorFactory.MeasureTabHeightFn mMeasureTabHeightFn;

    @NonNull
    private final HeightCalculatorFactory.GetTabCountFn mGetTabCountFn;

    @NonNull
    protected final SparseArray<TabMeasurement> mTabsHeightCache = new SparseArray<>();
    @Nullable
    private Bundle mPendingState;

    private int mPosition = 0;

    private float mPositionOffset = 0;

    protected BaseCardHeightCalculator(@NonNull ViewGroup channelGroup,
                                       @NonNull HeightCalculatorFactory.MeasureTabHeightFn measureTabHeightFn,
                                       @NonNull HeightCalculatorFactory.GetTabCountFn getTabCountFn) {
        mChannelGroup = channelGroup;
        mMeasureTabHeightFn = measureTabHeightFn;
        mGetTabCountFn = getTabCountFn;
    }

    private float getFontScale() {
        return mChannelGroup.getContext().getResources().getConfiguration().fontScale;
    }

    @Override
    @CallSuper
    public void saveInstanceState(@NonNull SparseArray<Parcelable> container) {
        Bundle bundle = new Bundle();
        for (int i = 0, n = mTabsHeightCache.size(); i < n; ++i) {
            mTabsHeightCache.valueAt(i).saveState(bundle, mTabsHeightCache.keyAt(i));
        }
        bundle.putFloat(KEY_FONT_SCALE, getFontScale());
        container.put(R.id.tab_height_cache, bundle);
    }

    @Override
    @CallSuper
    public void restoreInstanceState(@NonNull SparseArray<Parcelable> container) {
        mTabsHeightCache.clear();
        mPendingState = (Bundle) container.get(R.id.tab_height_cache);
        Float fontScale = mPendingState == null ? null : mPendingState.getFloat(KEY_FONT_SCALE);
        if (fontScale != null && fontScale != getFontScale()) {
            mPendingState = null;
        }
    }

    @Override
    public int measureHeight(int widthMeasureSpec, int heightMeasureSpec) {
        TabMeasurement measurement = mTabsHeightCache.get(widthMeasureSpec);
        if (measurement == null) {
            int tabCount = mGetTabCountFn.apply();

            if (tabCount == 0) {
                return 0;
            }

            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            measurement = new TabMeasurement(tabCount, tabIndex -> mMeasureTabHeightFn.apply(mChannelGroup, width, tabIndex));
            if (mPendingState != null) {
                measurement.restoreState(mPendingState, widthMeasureSpec);
                measurement.removeState(mPendingState, widthMeasureSpec);
                if (mPendingState.isEmpty()) {
                    mPendingState = null;
                }
            }

            mTabsHeightCache.put(widthMeasureSpec, measurement);
        }

        return logAndReturnHeight(getOptimalHeight(measurement, mPosition, mPositionOffset),
                                  mPosition, mPositionOffset);
    }

    @Override
    public void setPositionAndOffsetForMeasure(int position, float positionOffset) {
        Log.d(TAG, "request layout for tab " + position + " with position offset " + positionOffset);
        mPosition = position;
        mPositionOffset = positionOffset;
    }

    @Override
    public void dropMeasureCache() {
        Log.d(TAG, "reseting layout...");
        mPendingState = null;
        mTabsHeightCache.clear();
    }

    protected boolean isTabsHeightsIsUnknown() {
        return mTabsHeightCache.size() == 0;
    }

    protected abstract int getOptimalHeight(@NonNull TabMeasurement measurement,
                                            int position,
                                            float positionOffset);

    private static int logAndReturnHeight(int height, int position, float positionOffset) {
        Log.d(TAG, "New optimal height for tab " + position + " with position offset " +
                positionOffset + " is " + height);
        return height;
    }
}
