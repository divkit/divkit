package com.yandex.div.legacy;

import android.text.TextUtils;
import android.view.View;
import androidx.annotation.NonNull;
import com.yandex.alicekit.core.annotations.PublicApi;
import com.yandex.div.DivAction;
import com.yandex.div.legacy.view.DivView;

/**
 * Logs Div UI-events.
 */
@PublicApi
public interface DivLogger {

    DivLogger STUB = new DivLogger() { };

    /**
     * Is called when element is clicked.
     */
    default void logClick(DivView divView, View view, String logId) {
        //do nothing
    }

    /**
     * Is called when element is clicked.
     */
    default void logClick(DivView divView, View view, DivAction action) {
        String id = action.logId;
        if (!TextUtils.isEmpty(id)) {
            logClick(divView, view, id);
        }
    }

    /**
     * Is called when selected page in tabs div is changed.
     */
    default void logTabPageChanged(DivView divView, int selectedTab) {
        // do nothing
    }

    /**
     * Is called when active tab title clicked
     */
    default void logActiveTabTitleClicked(@NonNull DivView divView, int selectedTab, @NonNull DivAction divAction) {
        // do nothing
    }

    /**
     * Is called when title bar in tabs div is scrolled.
     */
    default void logTabTitlesScroll(DivView divView) {
        // do nothing
    }

    /**
     * Is called when gallery is scrolled.
     */
    default void logGalleryScroll(DivView divView) {
        // do nothing
    }
}
