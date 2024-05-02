package com.yandex.div.core;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yandex.div.core.annotations.PublicApi;
import com.yandex.div.core.view2.Div2View;
import com.yandex.div.json.expressions.ExpressionResolver;
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

    /** @noinspection unused*/
    default void logSliderDrag(Div2View divView, View view, @Nullable Float value) {
        // do nothing
    }

    /**
     * Is called when element is clicked.
     */
    default void logClick(
            @NonNull Div2View divView,
            @NonNull ExpressionResolver resolver,
            @NonNull View view,
            @NonNull DivAction action
    ) {
        // do nothing
    }

    /**
     * Is called when element is clicked.
     * @noinspection unused
     */
    default void logClick(
            @NonNull Div2View divView,
            @NonNull ExpressionResolver resolver,
            @NonNull View view,
            @NonNull DivAction action,
            @NonNull String actionUid
    ) {
        logClick(divView, resolver, view, action);
    }

    /**
     * Is called when element is long clicked.
     */
    default void logLongClick(
            @NonNull Div2View divView,
            @NonNull ExpressionResolver resolver,
            @NonNull View view,
            @NonNull DivAction action
    ) {
        // do nothing
    }

    /**
     * Is called when element is long clicked.
     * @noinspection unused
     */
    default void logLongClick(
            @NonNull Div2View divView,
            @NonNull ExpressionResolver resolver,
            @NonNull View view,
            @NonNull DivAction action,
            @NonNull String actionUid
    ) {
        logLongClick(divView, resolver, view, action);
    }

    /**
     * Is called when element is double clicked.
     */
    default void logDoubleClick(
            @NonNull Div2View divView,
            @NonNull ExpressionResolver resolver,
            @NonNull View view,
            @NonNull DivAction action
    ) {
        // do nothing
    }

    /**
     * Is called when element is double clicked.
     * @noinspection unused
     */
    default void logDoubleClick(
            @NonNull Div2View divView,
            @NonNull ExpressionResolver resolver,
            @NonNull View view,
            @NonNull DivAction action,
            @NonNull String actionUid
    ) {
        logDoubleClick(divView, resolver, view, action);
    }

    /**
     * Is called when element focus changed.
     */
    default void logFocusChanged(
            @NonNull Div2View divView,
            @NonNull ExpressionResolver resolver,
            @NonNull View view,
            @NonNull DivAction action,
            boolean haveFocus
    ) {
        // do nothing
    }

    /**
     * Is called when selected page in tabs div is changed.
     */
    default void logTabPageChanged(Div2View divView, int selectedTab) {
        // do nothing
    }

    /** @noinspection unused*/
    default void logActiveTabTitleClick(
            @NonNull Div2View divView,
            @NonNull ExpressionResolver resolver,
            int selectedTab,
            @NonNull DivAction action
    ) {
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
    default void logGalleryCompleteScroll(
            @NonNull Div2View divView,
            @NonNull ExpressionResolver resolver,
            @NonNull DivGallery divGallery,
            int firstVisibleItem,
            int lastVisibleItem,
            @NonNull @ScrollDirection String scrollDirection
    ) {
        // do nothing
    }

    /**
     * Is called when page is changed page. With parameters.
     * @param currentPageIndex - current page index
     * @param scrollDirection - next for left and up, back for right and down
     */
    default void logPagerChangePage(
            @NonNull Div2View divView,
            @NonNull ExpressionResolver resolver,
            @NonNull DivPager divPager,
            int currentPageIndex,
            @NonNull @ScrollDirection String scrollDirection) {
        // do nothing
    }

    /**
     * Called when the element is shown.
     */
    default void logViewShown(
            @NonNull Div2View divView,
            @NonNull ExpressionResolver resolver,
            @NonNull View view,
            @NonNull DivVisibilityAction visibilityAction
    ) {
        // do nothing
    }

    /**
     * Called when the element disappears.
     */
    default void logViewDisappeared(
            @NonNull Div2View divView,
            @NonNull ExpressionResolver resolver,
            @NonNull View view,
            @NonNull DivDisappearAction disappearAction
    ) {
        // do nothing
    }

    /**
     * Called when the element is shown.
     * @noinspection unused
     */
    default void logViewShown(
            @NonNull Div2View divView,
            @NonNull ExpressionResolver resolver,
            @NonNull View view,
            @NonNull DivVisibilityAction visibilityAction,
            @NonNull String actionUid
    ) {
        logViewShown(divView, resolver, view, visibilityAction);
    }

    /**
     * Called when the element disappears.
     * @noinspection unused
     */
    default void logViewDisappeared(
            @NonNull Div2View divView,
            @NonNull ExpressionResolver resolver,
            @NonNull View view,
            @NonNull DivDisappearAction disappearAction,
            @NonNull String actionUid
    ) {
        logViewDisappeared(divView, resolver, view, disappearAction);
    }

    /**
     * Is called when menu item is clicked.
     * @noinspection unused
     */
    default void logPopupMenuItemClick(
            @NonNull Div2View divView,
            @NonNull ExpressionResolver resolver,
            int position,
            @Nullable String text,
            @NonNull DivAction action
    ) {
        // do nothing
    }

    /**
     * Is called when div state (alert) is swiped away.
     * @noinspection unused
     */
    default void logSwipedAway(
            @NonNull Div2View div2View,
            @NonNull ExpressionResolver resolver,
            @NonNull View view,
            @NonNull DivAction action
    ) {
        // do nothing
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
