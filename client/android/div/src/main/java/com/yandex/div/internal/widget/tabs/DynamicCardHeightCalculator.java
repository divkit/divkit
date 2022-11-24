package com.yandex.div.internal.widget.tabs;

import android.view.ViewGroup;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

@MainThread
public class DynamicCardHeightCalculator extends BaseCardHeightCalculator {

    public DynamicCardHeightCalculator(@NonNull ViewGroup channelGroup,
                                       @NonNull HeightCalculatorFactory.MeasureTabHeightFn measureTabHeights,
                                       @NonNull HeightCalculatorFactory.GetTabCountFn getTabCountFn) {
        super(channelGroup, measureTabHeights, getTabCountFn);
    }

    @Override
    public boolean shouldRequestLayoutOnScroll(int position, float positionOffset) {
        return true;
    }

    @Override
    public int measureHeight(int widthMeasureSpec, int heightMeasureSpec) {
        dropMeasureCache();
        return super.measureHeight(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected int getOptimalHeight(@NonNull TabMeasurement measurement,
                                   int position,
                                   float positionOffset) {
        if (positionOffset < 0.01f) {
            return measurement.getTabHeight(position);
        }
        int firstHeight = measurement.getTabHeight(position);
        int secondHeight = measurement.getTabHeight(position + 1);

        return Math.round(firstHeight + (secondHeight - firstHeight) * positionOffset);
    }
}
