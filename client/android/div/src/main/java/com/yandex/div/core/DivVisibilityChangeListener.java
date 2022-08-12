package com.yandex.div.core;

import android.view.View;
import com.yandex.div.core.annotations.PublicApi;
import com.yandex.div2.Div;
import java.util.Map;

/**
 * Observe all visible div-views at the moment.
 */
@PublicApi
public interface DivVisibilityChangeListener {
    DivVisibilityChangeListener STUB = actions -> { };

    void onViewsVisibilityChanged(Map<View, Div> visibleViews);
}
