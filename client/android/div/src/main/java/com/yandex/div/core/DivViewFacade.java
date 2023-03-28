package com.yandex.div.core;

import android.content.res.Configuration;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yandex.div.DivDataTag;
import com.yandex.div.core.annotations.PublicApi;
import com.yandex.div.core.images.LoadReference;
import com.yandex.div.core.state.DivStatePath;
import com.yandex.div.core.state.DivViewState;
import com.yandex.div.internal.widget.menu.OverflowMenuSubscriber;
import com.yandex.div.json.expressions.ExpressionResolver;

/**
 * Main class to interact with DivView and DivView2
 */
@PublicApi
public interface DivViewFacade {

    /**
     * Method for div builders to allow DivView to control image loads.
     * @param loadReference not started image load reference
     * @param targetView view, that loaded image. Used to link reference
     * @deprecated Use {@link DivViewFacade#addLoadReference(LoadReference, View)}
     */
    @Deprecated()
    default void addImageLoadReference(@NonNull LoadReference loadReference, @NonNull View targetView) {
    }

    /**
     * Method for loading. Now it used for images
     * @param loadReference not started image load reference
     * @param targetView view, that loaded. Used to link reference
     */
    default void addLoadReference(@NonNull LoadReference loadReference, @NonNull View targetView) {
        addImageLoadReference(loadReference, targetView);
    }

    /**
     * @param config Setter for DivConfig
     */
    void setConfig(@NonNull DivViewConfig config);

    /**
     * @return getter for DivConfig
     */
    @NonNull
    DivViewConfig getConfig();

    /**
     * @return getter for DivTag
     */
    @NonNull
    DivDataTag getDivTag();

    /**
     * Used to handle all actions from divs inside DivView
     * @param uri to handle
     */
    void handleUri(@NonNull Uri uri);

    /**
     * Cleans up this view from the previously bounded data.
     */
    void cleanup();

    /**
     * Switches view to state without saving state into cache
     * @param id in divState
     */
    default void switchToState(@IntRange(from = 0) long id) {
        switchToState(id, true);
    }

    /**
     * Switches view to state
     * @param id in divState
     * @param temporary true,  if updated state should be kept only in in-memory storage
     *                  false, if updated state should be saved into cache
     */
    void switchToState(@IntRange(from = 0) long id, boolean temporary);

    /**
     * Switches view to initial state.
     */
    void switchToInitialState();

    /**
     * Switches view to state
     * @param statePath see {@link com.yandex.div.core.state.DivStatePath}
     */
    default void switchToState(@NonNull DivStatePath statePath, boolean temporary) {
        switchToState(statePath.getTopLevelStateId(), temporary);
    }

    /**
     * Add listeners for handle overflow menu click
     */
    void subscribe(@NonNull OverflowMenuSubscriber.Listener listener);

    /**
     * Remove listeners for handle overflow menu click
     */
    void clearSubscriptions();

    /**
     * Invalidate view on change configuration
     */
    void onConfigurationChangedOutside(@NonNull Configuration newConfig);

    /**
     * Close div overflow menu
     */
    void dismissPendingOverflowMenus();

    /**
     * Checks, whether a point is inside a scrollable child
     * Should be used with {@link androidx.recyclerview.widget.FixScrollTouchHelper}
     * @return true if the point is inside a scrollable child
     */
    boolean hasScrollableViewUnder(@NonNull MotionEvent event);

    /**
     * Reset to first in states[]
     */
    void resetToInitialState();

    /**
     * @return Current div state id
     */
    long getCurrentStateId();

    /**
     * @return DivViewState from DivStateManager
     */
    @Nullable
    DivViewState getCurrentState();

    /**
     * @return wrapped {@link View}.
     */
    @NonNull
    View getView();

    @NonNull
    default ExpressionResolver getExpressionResolver() {
        return ExpressionResolver.EMPTY;
    }

    default void showTooltip(@NonNull String tooltipId) {
        //do nothing by default
    }

    default void hideTooltip(@NonNull String tooltipId) {
        //do nothing by default
    }

    default void cancelTooltips() {
        // do nothing by default
    }
}
