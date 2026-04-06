package com.yandex.div.core;

import android.view.View;
import com.yandex.div2.Div;

import java.util.Map;

/**
 * Observe all visible div-views at the moment.
 */
public interface DivVisibilityChangeListener {
    DivVisibilityChangeListener STUB = actions -> { };

    void onViewsVisibilityChanged(Map<View, Div> visibleViews);
}
