package com.yandex.div.view.tabs;

import android.os.Bundle;
import androidx.annotation.NonNull;

class TabMeasurement {
    private static final String FIRST_TAB_HEIGHT_PREFIX = "FIRST_TAB_HEIGHT_PREFIX";
    private static final String MAX_TAB_HEIGHT_PREFIX = "MAX_TAB_HEIGHT_PREFIX";

    private final int mTabCount;
    private int mFirstTabHeight = -1;
    private int mMaxTabHeight = -1;
    private int[] mTabHeight;
    @NonNull
    private final TabMeasurementFunction mTabMeasurementFunction;

    TabMeasurement(int tabCount, @NonNull TabMeasurementFunction tabMeasurementFunction) {
        mTabCount = tabCount;
        mTabMeasurementFunction = tabMeasurementFunction;
        mTabHeight = new int[mTabCount];
    }

    int getFirstTabHeight() {
        if (mFirstTabHeight < 0) {
            mFirstTabHeight = mTabMeasurementFunction.getTabHeight(0);
        }
        return mFirstTabHeight;
    }

    int getTabHeight(int position) {
        if (mTabCount == 0) {
            return 0;
        }
        if (position < 0) {
            return getTabHeight(0);
        }
        if (position >= mTabCount) {
            return getTabHeight(mTabCount);
        }
        if (mTabHeight[position] <= 0) {
            mTabHeight[position] = mTabMeasurementFunction.getTabHeight(position);
        }
        return mTabHeight[position];
    }

    int getMaxTabHeight() {
        if (mMaxTabHeight < 0) {
            int max = getFirstTabHeight();
            for (int i = 1; i < mTabCount; ++i) {
                max = Math.max(max, mTabMeasurementFunction.getTabHeight(i));
            }
            mMaxTabHeight = max;
        }
        return mMaxTabHeight;
    }

    void saveState(@NonNull Bundle bundle, int key) {
        if (mFirstTabHeight >= 0) {
            bundle.putInt(FIRST_TAB_HEIGHT_PREFIX + key, mFirstTabHeight);
        }
        if (mMaxTabHeight >= 0) {
            bundle.putInt(MAX_TAB_HEIGHT_PREFIX + key, mMaxTabHeight);
        }
    }

    void restoreState(@NonNull Bundle bundle, int key) {
        mFirstTabHeight = bundle.getInt(FIRST_TAB_HEIGHT_PREFIX + key, -1);
        mMaxTabHeight = bundle.getInt(MAX_TAB_HEIGHT_PREFIX + key, -1);
    }

    void removeState(@NonNull Bundle bundle, int key) {
        bundle.remove(FIRST_TAB_HEIGHT_PREFIX + key);
        bundle.remove(MAX_TAB_HEIGHT_PREFIX + key);
    }

    interface TabMeasurementFunction {
        int getTabHeight(int tab);
    }
}
