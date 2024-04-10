package com.yandex.div.core;

import android.net.Uri;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yandex.div.core.annotations.PublicApi;
import com.yandex.div.core.view2.Div2View;
import com.yandex.div2.DivAction;
import com.yandex.div2.DivData;
import com.yandex.div2.DivDisappearAction;
import com.yandex.div2.DivGallery;
import com.yandex.div2.DivPager;
import com.yandex.div2.DivPatch;
import com.yandex.div2.DivVisibilityAction;

/**
 * Logs Div2 UI-events.
 */
@PublicApi
public interface Div2Logger {

    Div2Logger STUB = new Div2Logger() { };

    default void logSliderDrag(Div2View divView, View view, @Nullable Float value) {
        // do nothing
    }

    /**
     * Is called when element is clicked.
     */
    default void logClick(Div2View divView, View view, DivAction action) {
        // do nothing
    }

    /**
     * Is called when element is clicked.
     */
    default void logClick(Div2View divView, View view, DivAction action, String actionUid) {
        logClick(divView, view, action);
    }

    /**
     * Is called when element is long clicked.
     */
    default void logLongClick(Div2View divView, View view, DivAction action) {
        // do nothing
    }

    /**
     * Is called when element is long clicked.
     */
    default void logLongClick(Div2View divView, View view, DivAction action, String actionUid) {
        logLongClick(divView, view, action);
    }

    /**
     * Is called when element is double clicked.
     */
    default void logDoubleClick(Div2View divView, View view, DivAction action) {
        // do nothing
    }

    /**
     * Is called when element is double clicked.
     */
    default void logDoubleClick(Div2View divView, View view, DivAction action, String actionUid) {
        logDoubleClick(divView, view, action);
    }

    /**
     * Is called when element focus changed.
     */
    default void logFocusChanged(Div2View divView, View view, DivAction action, Boolean haveFocus) {
        // do nothing
    }

    /**
     * Is called when selected page in tabs div is changed.
     */
    default void logTabPageChanged(Div2View divView, int selectedTab) {
        // do nothing
    }

    default void logActiveTabTitleClick(@NonNull Div2View divView, int selectedTab, @NonNull DivAction action) {
        // do nothing
    }

    /**
     * Is called when title bar in tabs div is scrolled.
     */
    default void logTabTitlesScroll(Div2View divView) {
        // do nothing
    }

    /**
     * Is called when gallery is scrolled.
     */
    default void logGalleryScroll(Div2View divView) {
        // do nothing
    }

    /**
     * Is called when gallery is FINISHED scrolled. With parameters.
     * @param firstVisibleItem - first partly visible item when scroll is completed
     * @param lastVisibleItem - last partly visible item when scroll is completed
     * @param scrollDirection - next for left and up, back for right and down
     */
    default void logGalleryCompleteScroll(Div2View divView, DivGallery divGallery,
                                          int firstVisibleItem, int lastVisibleItem, @ScrollDirection String scrollDirection) {
        // do nothing
    }

    /**
     * Is called when page is changed page. With parameters.
     * @param currentPageIndex - current page index
     * @param scrollDirection - next for left and up, back for right and down
     */
    default void logPagerChangePage(Div2View divView, DivPager divPager,
                                    int currentPageIndex, @ScrollDirection String scrollDirection) {
        // do nothing
    }

    /**
     * Called when the element is shown.
     */
    default void logViewShown(Div2View divView, View view, DivVisibilityAction visibilityAction) {
        // do nothing
    }

    /**
     * Called when the element disappears.
     */
    default void logViewDisappeared(Div2View divView, View view, DivDisappearAction disappearAction) {
        // do nothing
    }

    /**
     * Called when the element is shown.
     */
    default void logViewShown(Div2View divView, View view, DivVisibilityAction visibilityAction, String actionUid) {
        logViewShown(divView, view, visibilityAction);
    }

    /**
     * Called when the element disappears.
     */
    default void logViewDisappeared(Div2View divView, View view, DivDisappearAction disappearAction, String actionUid) {
        logViewDisappeared(divView, view, disappearAction);
    }

    /**
     * Is called when menu item is clicked.
     */
    @Deprecated
    default void logPopupMenuItemClick(Div2View divView, int position,
                                       @Nullable String text, @Nullable Uri url) {
        // do nothing
    }

    /**
     * Is called when menu item is clicked.
     */
    default void logPopupMenuItemClick(Div2View divView, int position,
                                       @Nullable String text, DivAction action) {
        Uri url = action.url != null ? action.url.evaluate(divView.getExpressionResolver()) : null;
        logPopupMenuItemClick(divView, position, text, url);
    }

    /**
     * Is called when div state (alert) is swiped away.
     */
    default void logSwipedAway(Div2View div2View, View view, DivAction action) {
        //do nothing
    }

    /**
     * Is called when trigger activates.
     */
    default void logTrigger(Div2View divView, DivAction action) {
        //do nothing
    }

    default void logBindingResult(
        @NonNull Div2View divView,
        @Nullable DivData oldData,
        @Nullable DivData newData,
        @NonNull String result,
        @Nullable String eventsMessage
    ) {
        //do nothing
    }

    default void logPatchResult(
        @NonNull Div2View divView,
        @NonNull DivPatch patch,
        @NonNull String result,
        @Nullable String eventsMessage
    ) {
        //do nothing
    }
}
