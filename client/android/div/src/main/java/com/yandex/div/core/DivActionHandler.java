package com.yandex.div.core;

import android.net.Uri;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yandex.div.core.annotations.PublicApi;
import com.yandex.div.core.downloader.DivDownloadActionHandler;
import com.yandex.div.core.expression.storedvalues.StoredValuesActionHandler;
import com.yandex.div.core.state.DivStatePath;
import com.yandex.div.core.state.PathFormatException;
import com.yandex.div.core.view2.Div2View;
import com.yandex.div.core.view2.items.DivItemChangeActionHandler;
import com.yandex.div.data.VariableMutationException;
import com.yandex.div.internal.Assert;
import com.yandex.div2.DivAction;
import com.yandex.div2.DivVisibilityAction;
import com.yandex.div2.DivDisappearAction;
import com.yandex.div2.DivSightAction;
import org.json.JSONObject;

/**
 * Handles URIs and payloads, that are triggered by click events in {@link Div2View}.
 */
@PublicApi
public class DivActionHandler {
    private static final String SCHEME_DIV_ACTION = "div-action";

    private static final String AUTHORITY_SWITCH_STATE = "set_state";
    private static final String AUTHORITY_SHOW_TOOLTIP = "show_tooltip";
    private static final String AUTHORITY_HIDE_TOOLTIP = "hide_tooltip";
    private static final String AUTHORITY_SET_VARIABLE = "set_variable";
    private static final String AUTHORITY_TIMER = "timer";
    private static final String AUTHORITY_VIDEO = "video";

    private static final String PARAM_STATE_ID = "state_id";
    private static final String PARAM_ID = "id";
    private static final String PARAM_ACTION = "action";
    private static final String PARAM_TEMPORARY = "temporary";
    private static final String PARAM_VARIABLE_NAME = "name";
    private static final String PARAM_VARIABLE_VALUE = "value";
    private static final String PARAM_MULTIPLE = "multiple";

    public boolean getUseActionUid() {
        return false;
    }

    /**
     * Handles the given URI.
     * Call super implementation to automatically handle internal div schemes when overriding.
     *
     * @param uri  URI to handle
     * @param view calling DivView
     * @return TRUE if uri was handled
     */
    @CallSuper
    @Deprecated
    public boolean handleUri(@NonNull Uri uri, @NonNull DivViewFacade view) {
        return handleActionUrl(uri, view);
    }

    /**
     * Handles the given div action.
     * Call super implementation to automatically handle internal div schemes when overriding.
     *
     * @param action full div action to handle
     * @param view calling DivView
     * @return TRUE if uri was handled
     */
    @CallSuper
    public boolean handleAction(@NonNull DivAction action, @NonNull DivViewFacade view) {
        Uri url = action.url != null ? action.url.evaluate(view.getExpressionResolver()) : null;
        if (DivDownloadActionHandler.canHandle(url, view)) {
            return DivDownloadActionHandler.handleAction(action, (Div2View) view);
        }
        return handleActionUrl(url, view);
    }

    /**
     * Handles the given div action.
     * Call super implementation to automatically handle internal div schemes when overriding.
     *
     * @param action full div action to handle
     * @param view calling DivView
     * @param actionUid action UUID string
     * @return TRUE if uri was handled
     */
    @CallSuper
    public boolean handleAction(@NonNull DivAction action, @NonNull DivViewFacade view, @NonNull String actionUid) {
        return handleAction(action, view);
    }

    /**
     * Handles the given div visibility action.
     * Call super implementation to automatically handle internal div schemes when overriding.
     *
     * @param action full div visibility action to handle
     * @param view calling DivView
     * @return TRUE if uri was handled
     */
    @CallSuper
    public boolean handleAction(@NonNull DivVisibilityAction action, @NonNull DivViewFacade view) {
        return handleAction((DivSightAction) action, view);
    }

    /**
     * Handles the given div disappear action.
     * Call super implementation to automatically handle internal div schemes when overriding.
     *
     * @param action full div disappear action to handle
     * @param view calling DivView
     * @return TRUE if uri was handled
     */
    @CallSuper
    public boolean handleAction(@NonNull DivDisappearAction action, @NonNull DivViewFacade view) {
        return handleAction((DivSightAction) action, view);
    }

    /**
     * Handles the given div sight action.
     * Call super implementation to automatically handle internal div schemes when overriding.
     *
     * @param action full div sight action to handle
     * @param view calling DivView
     * @return TRUE if uri was handled
     */
    @CallSuper
    public boolean handleAction(@NonNull DivSightAction action, @NonNull DivViewFacade view) {
        Uri url = action.getUrl() != null ? action.getUrl().evaluate(view.getExpressionResolver()) : null;
        if (DivDownloadActionHandler.canHandle(url, view)) {
            return DivDownloadActionHandler.handleVisibilityAction(action, (Div2View) view);
        }
        return handleActionUrl(url, view);
    }

    /**
     * Handles the given div visibility action.
     * Call super implementation to automatically handle internal div schemes when overriding.
     *
     * @param action full div visibility action to handle
     * @param view calling DivView
     * @param actionUid action UUID string
     * @return TRUE if uri was handled
     */
    @CallSuper
    public boolean handleAction(@NonNull DivVisibilityAction action, @NonNull DivViewFacade view, @NonNull String actionUid) {
        return handleAction(action, view);
    }

    /**
     * Handles the given div disappear action.
     * Call super implementation to automatically handle internal div schemes when overriding.
     *
     * @param action full div disappear action to handle
     * @param view calling DivView
     * @param actionUid action UUID string
     * @return TRUE if uri was handled
     */
    @CallSuper
    public boolean handleAction(@NonNull DivDisappearAction action, @NonNull DivViewFacade view, @NonNull String actionUid) {
        return handleAction(action, view);
    }

    /**
     * Handles the given div sight action.
     * Call super implementation to automatically handle internal div schemes when overriding.
     *
     * @param action full div disappear action to handle
     * @param view calling DivView
     * @param actionUid action UUID string
     * @return TRUE if uri was handled
     */
    @CallSuper
    public boolean handleAction(@NonNull DivSightAction action, @NonNull DivViewFacade view, @NonNull String actionUid) {
        return handleAction(action, view);
    }

    /**
     * Handles the given json.
     *
     * @param payload json to handle
     */
    public void handlePayload(@NonNull JSONObject payload) { /* not implemented */ }

    /**
     * Handles the URI with {@code div-action} scheme.
     *
     * @param uri  URI to handle
     * @param view calling DivView
     * @return TRUE if uri was handled
     */
    public final boolean handleActionUrl(@Nullable Uri uri, @NonNull DivViewFacade view) {
        if (uri == null) {
            return false;
        }

        //noinspection SimplifiableIfStatement
        if (SCHEME_DIV_ACTION.equals(uri.getScheme())) {
            return handleAction(uri, view);
        }

        return false;
    }

    private boolean handleAction(@NonNull Uri uri, @NonNull DivViewFacade view) {
        String action = uri.getAuthority();
        if (AUTHORITY_SWITCH_STATE.equals(action)) {
            String stateId = uri.getQueryParameter(PARAM_STATE_ID);
            if (stateId == null) {
                Assert.fail(PARAM_STATE_ID + " param is required");
                return false;
            }
            boolean temporary = uri.getBooleanQueryParameter(PARAM_TEMPORARY, true);
            try {
                view.switchToState(DivStatePath.parse(stateId), temporary);
            } catch (PathFormatException e) {
                Assert.fail("Invalid format of " + stateId, e);
                return false;
            }
            return true;
        } else if (AUTHORITY_SHOW_TOOLTIP.equals(action)) {
            String tooltipId = uri.getQueryParameter(PARAM_ID);
            if (tooltipId == null) {
                Assert.fail(PARAM_ID + " param is required");
                return false;
            }
            boolean multiple = uri.getBooleanQueryParameter(PARAM_MULTIPLE, false);
            view.showTooltip(tooltipId, multiple);
            return true;
        } else if (AUTHORITY_HIDE_TOOLTIP.equals(action)) {
            String tooltipId = uri.getQueryParameter(PARAM_ID);
            if (tooltipId == null) {
                Assert.fail(PARAM_ID + " param is required");
                return false;
            }
            view.hideTooltip(tooltipId);
            return true;
        } else if (AUTHORITY_SET_VARIABLE.equals(action)) {
            String name = uri.getQueryParameter(PARAM_VARIABLE_NAME);
            if (name == null) {
                Assert.fail(PARAM_VARIABLE_NAME + " param is required");
                return false;
            }
            String value = uri.getQueryParameter(PARAM_VARIABLE_VALUE);
            if (value == null) {
                Assert.fail(PARAM_VARIABLE_VALUE + " param unspecified for " + name);
                return false;
            }

            Div2View div2View = (view instanceof Div2View) ? (Div2View) view : null;

            if (div2View == null) {
                Assert.fail("Variable '" + name + "' mutation failed! View("+
                                    view.getClass().getSimpleName()+") not supports variables!");
                return false;
            }
            try {
                div2View.setVariable(name, value);
            } catch (VariableMutationException e) {
                Assert.fail("Variable '" + name + "' mutation failed: " + e.getMessage(), e);
                return false;
            }
            return true;
        } else if (AUTHORITY_TIMER.equals(action)) {
            String id = uri.getQueryParameter(PARAM_ID);
            if (id == null) {
                Assert.fail(PARAM_ID + " param is required");
                return false;
            }
            String command = uri.getQueryParameter(PARAM_ACTION);
            if (command == null) {
                Assert.fail(PARAM_ACTION + " param is required");
                return false;
            }

            Div2View div2View = (view instanceof Div2View) ? (Div2View) view : null;

            if (div2View == null) {
                Assert.fail("Timer '" + id + "' state changing failed! View("+
                        view.getClass().getSimpleName()+") not supports timers!");
                return false;
            }

            div2View.applyTimerCommand(id, command);
            return true;
        } else if (AUTHORITY_VIDEO.equals(action)) {
            Div2View div2View = (view instanceof Div2View) ? (Div2View) view : null;
            if (div2View == null) {
                Assert.fail("Handler view is not instance of Div2View");
                return false;
            }

            String id = uri.getQueryParameter("id");
            if (id == null) {
                Assert.fail("Video action has no id param");
                return false;
            }

            String command = uri.getQueryParameter("action");
            if (command == null) {
                Assert.fail("Video action has no action param");
                return false;
            }

            return div2View.applyVideoCommand(id, command);
        } else if (DivItemChangeActionHandler.canHandle(action)) {
            return DivItemChangeActionHandler.handleAction(uri, view);
        } else if (StoredValuesActionHandler.canHandle(action)) {
            return StoredValuesActionHandler.handleAction(uri, view);
        }

        return false;
    }
}
