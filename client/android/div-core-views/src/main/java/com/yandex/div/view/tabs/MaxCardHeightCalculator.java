package com.yandex.div.view.tabs;

import android.view.ViewGroup;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import com.yandex.div.core.util.Assert;

@MainThread
public class MaxCardHeightCalculator extends BaseCardHeightCalculator {

    public MaxCardHeightCalculator(@NonNull ViewGroup channelGroup,
                                   @NonNull HeightCalculatorFactory.MeasureTabHeightFn measureTabHeights,
                                   @NonNull HeightCalculatorFactory.GetTabCountFn getTabCountFn) {
        super(channelGroup, measureTabHeights, getTabCountFn);
    }

    @Override
    public boolean shouldRequestLayoutOnScroll(int position, float positionOffset) {
        return isTabsHeightsIsUnknown() || ((position == 0 || position == 1 && positionOffset <= 0.0f) && firstTabDiffers());
    }

    @Override
    protected int getOptimalHeight(@NonNull TabMeasurement measurement,
                                   int position,
                                   float positionOffset) {
        if (position > 0) {
            return measurement.getMaxTabHeight();
        }

        if (positionOffset < 0.01f) {
            return measurement.getFirstTabHeight();
        }

        int firstHeight = measurement.getFirstTabHeight();
        int maxHeight = measurement.getMaxTabHeight();

        return Math.round(firstHeight + (maxHeight - firstHeight) * positionOffset);
    }

    private boolean firstTabDiffers() {
        Assert.assertTrue(mTabsHeightCache.size() > 0);
        TabMeasurement measurement = mTabsHeightCache.valueAt(mTabsHeightCache.size() - 1);
        return measurement.getFirstTabHeight() != measurement.getMaxTabHeight();
    }
}
