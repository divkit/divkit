package com.yandex.div.core;

import android.net.Uri;
import android.view.View;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yandex.div.core.actions.DivActionTypedHandlerProxy;
import com.yandex.div.core.annotations.PublicApi;
import com.yandex.div.core.downloader.DivDownloadActionHandler;
import com.yandex.div.core.expression.storedvalues.StoredValuesActionHandler;
import com.yandex.div.core.state.DivStatePath;
import com.yandex.div.core.state.PathFormatException;
import com.yandex.div.core.view2.BindingContext;
import com.yandex.div.core.view2.Div2View;
import com.yandex.div.core.view2.ViewLocator;
import com.yandex.div.core.view2.divs.widgets.DivHolderView;
import com.yandex.div.core.view2.items.DivItemChangeActionHandler;
import com.yandex.div.data.VariableMutationException;
import com.yandex.div.internal.Assert;
import com.yandex.div.internal.core.VariableMutationHandler;
import com.yandex.div.json.expressions.ExpressionResolver;
import com.yandex.div2.DivAction;
import com.yandex.div2.DivDisappearAction;
import com.yandex.div2.DivSightAction;
import com.yandex.div2.DivVisibilityAction;
import org.json.JSONObject;

/**
 * Handles URIs and payloads, that are triggered by click events in {@link Div2View}.
 */
@PublicApi
public class DivActionHandler {

    public static class DivActionReason {
        public static final String BLUR = "blur";
        public static final String CLICK = "click";
        public static final String DOUBLE_CLICK = "double_click";
        public static final String EXTERNAL = "external";
        public static final String FOCUS = "focus";
        public static final String HOVER = "hover";
        public static final String LONG_CLICK = "long_click";
        public static final String MENU = "menu";
        public static final String PATCH = "patch";
        public static final String PRESS = "press";
        public static final String RELEASE = "release";
        public static final String SELECTION = "selection";
        public static final String STATE_SWIPE_OUT = "state_swipe_out";
        public static final String TIMER = "timer";
        public static final String TRIGGER = "trigger";
        public static final String UNHOVER = "unhover";
        public static final String VIDEO = "video";
        public static final String ANIMATION_END = "animation_end";
        public static final String ANIMATION_CANCEL = "animation_cancel";
        public static final String ENTER = "enter";
    }

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
        return handleActionUrl(uri, view, view.getExpressionResolver());
    }

    /**
     * Handles the given div action.
     * Call super implementation to automatically handle internal div schemes when overriding.
     *
     * @param action full div action to handle
     * @param view calling DivView
     * @param resolver resolver for current action
     * @return TRUE if uri was handled
     */
    @CallSuper
    public boolean handleAction(
            @NonNull DivAction action,
            @NonNull DivViewFacade view,
            @NonNull ExpressionResolver resolver
    ) {
        ExpressionResolver scopedResolver = findExpressionResolverById((Div2View) view, action.scopeId);
        ExpressionResolver localResolver = scopedResolver == null ? resolver : scopedResolver;

        if (DivActionTypedHandlerProxy.handleAction(action, view, localResolver)) {
            return true;
        }
        Uri url = action.url != null ? action.url.evaluate(resolver) : null;
        if (DivDownloadActionHandler.canHandle(url, view)) {
            return DivDownloadActionHandler.handleAction(action, (Div2View) view, localResolver);
        }
        return handleAction(action.scopeId, url, view, localResolver);
    }

    /**
     * Handles the given div action.
     * Call super implementation to automatically handle internal div schemes when overriding.
     *
     * @param action full div action to handle
     * @param view calling DivView
     * @param resolver resolver for current action
     * @param actionUid action UUID string
     * @return TRUE if uri was handled
     * @noinspection unused
     */
    @CallSuper
    public boolean handleAction(
            @NonNull DivAction action,
            @NonNull DivViewFacade view,
            @NonNull ExpressionResolver resolver,
            @NonNull String actionUid
    ) {
        return handleAction(action, view, resolver);
    }

    /**
     * Handles the given div action.
     * Call super implementation to automatically handle internal div schemes when overriding.
     *
     * @param action full div action to handle
     * @param view calling DivView
     * @param resolver resolver for current action
     * @param reason reason of div action call
     * @return TRUE if uri was handled
     * @noinspection unused
     */
    @CallSuper
    public boolean handleActionWithReason(
            @NonNull DivAction action,
            @NonNull DivViewFacade view,
            @NonNull ExpressionResolver resolver,
            @NonNull String reason
    ) {
        return handleAction(action, view, resolver);
    }

    /**
     * Handles the given div action.
     * Call super implementation to automatically handle internal div schemes when overriding.
     *
     * @param action full div action to handle
     * @param view calling DivView
     * @param resolver resolver for current action
     * @param actionUid action UUID string
     * @param reason reason of div action call
     * @return TRUE if uri was handled
     * @noinspection unused
     */
    @CallSuper
    public boolean handleActionWithReason(
            @NonNull DivAction action,
            @NonNull DivViewFacade view,
            @NonNull ExpressionResolver resolver,
            @NonNull String actionUid,
            @NonNull String reason) {
        return handleAction(action, view, resolver, actionUid);
    }

    /**
     * Handles the given div visibility action.
     * Call super implementation to automatically handle internal div schemes when overriding.
     *
     * @param action full div visibility action to handle
     * @param view calling DivView
     * @param resolver resolver for current action
     * @return TRUE if uri was handled
     */
    @CallSuper
    public boolean handleAction(
            @NonNull DivVisibilityAction action,
            @NonNull DivViewFacade view,
            @NonNull ExpressionResolver resolver
    ) {
        return handleAction((DivSightAction) action, view, resolver);
    }

    /**
     * Handles the given div disappear action.
     * Call super implementation to automatically handle internal div schemes when overriding.
     *
     * @param action full div disappear action to handle
     * @param view calling DivView
     * @param resolver resolver for current action
     * @return TRUE if uri was handled
     */
    @CallSuper
    public boolean handleAction(
            @NonNull DivDisappearAction action,
            @NonNull DivViewFacade view,
            @NonNull ExpressionResolver resolver
    ) {
        return handleAction((DivSightAction) action, view, resolver);
    }

    /**
     * Handles the given div sight action.
     * Call super implementation to automatically handle internal div schemes when overriding.
     *
     * @param action full div sight action to handle
     * @param view calling DivView
     * @param resolver resolver for current action
     * @return TRUE if uri was handled
     */
    @CallSuper
    public boolean handleAction(
            @NonNull DivSightAction action,
            @NonNull DivViewFacade view,
            @NonNull ExpressionResolver resolver
    ) {
        ExpressionResolver scopedResolver = findExpressionResolverById((Div2View) view, action.getScopeId());
        ExpressionResolver localResolver = scopedResolver == null ? resolver : scopedResolver;

        if (DivActionTypedHandlerProxy.handleVisibilityAction(action, view, localResolver)) {
            return true;
        }
        Uri url = action.getUrl() != null ? action.getUrl().evaluate(resolver) : null;
        if (DivDownloadActionHandler.canHandle(url, view)) {
            return DivDownloadActionHandler.handleVisibilityAction(action, (Div2View) view, localResolver);
        }
        return handleAction(action.getScopeId(), url, view, resolver);
    }

    /**
     * Handles the given div visibility action.
     * Call super implementation to automatically handle internal div schemes when overriding.
     *
     * @param action full div visibility action to handle
     * @param view calling DivView
     * @param resolver resolver for current action
     * @param actionUid action UUID string
     * @return TRUE if uri was handled
     * @noinspection unused
     */
    @CallSuper
    public boolean handleAction(
            @NonNull DivVisibilityAction action,
            @NonNull DivViewFacade view,
            @NonNull ExpressionResolver resolver,
            @NonNull String actionUid
    ) {
        return handleAction(action, view, resolver);
    }

    /**
     * Handles the given div disappear action.
     * Call super implementation to automatically handle internal div schemes when overriding.
     *
     * @param action full div disappear action to handle
     * @param view calling DivView
     * @param resolver resolver for current action
     * @param actionUid action UUID string
     * @return TRUE if uri was handled
     * @noinspection unused
     */
    @CallSuper
    public boolean handleAction(
            @NonNull DivDisappearAction action,
            @NonNull DivViewFacade view,
            @NonNull ExpressionResolver resolver,
            @NonNull String actionUid
    ) {
        return handleAction(action, view, resolver);
    }

    /**
     * Handles the given div sight action.
     * Call super implementation to automatically handle internal div schemes when overriding.
     *
     * @param action full div disappear action to handle
     * @param view calling DivView
     * @param resolver resolver for current action
     * @param actionUid action UUID string
     * @return TRUE if uri was handled
     * @noinspection unused
     */
    @CallSuper
    public boolean handleAction(
            @NonNull DivSightAction action,
            @NonNull DivViewFacade view,
            @NonNull ExpressionResolver resolver,
            @NonNull String actionUid
    ) {
        return handleAction(action, view, resolver);
    }

    /**
     * Handles the given json.
     *
     * @param payload json to handle
     *
     * @noinspection unused
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
        return handleActionUrl(uri, view, view.getExpressionResolver());
    }

    /**
     * Handles the URI with {@code div-action} scheme.
     *
     * @param uri  URI to handle
     * @param view calling DivView
     * @param resolver resolver for current action
     * @return TRUE if uri was handled
     */
    public final boolean handleActionUrl(
            @Nullable Uri uri,
            @NonNull DivViewFacade view,
            @NonNull ExpressionResolver resolver
    ) {
        return handleActionUrl(null, uri, view, resolver);
    }

    /**
     * Handles the URI with {@code div-action} scheme.
     *
     * @param scopeId id of div that denotes scope of given action
     * @param uri  URI to handle
     * @param view calling DivView
     * @param resolver resolver for current action
     * @return TRUE if uri was handled
     */
    public final boolean handleActionUrl(
            @Nullable String scopeId,
            @Nullable Uri uri,
            @NonNull DivViewFacade view,
            @NonNull ExpressionResolver resolver
    ) {
        ExpressionResolver scopedResolver = findExpressionResolverById((Div2View) view, scopeId);
        ExpressionResolver localResolver = scopedResolver == null ? resolver : scopedResolver;
        return handleAction(scopeId, uri, view, localResolver);
    }

    private boolean handleAction(
            @Nullable String scopeId,
            @Nullable Uri uri,
            @NonNull DivViewFacade view,
            @NonNull ExpressionResolver resolver
    ) {
        if (uri == null) {
            return false;
        }

        //noinspection SimplifiableIfStatement
        if (SCHEME_DIV_ACTION.equals(uri.getScheme())) {
            return handleActionInternal(scopeId, uri, view, resolver);
        }

        return false;
    }

    private boolean handleActionInternal(
            @Nullable String scopeId,
            @Nullable Uri uri,
            @NonNull DivViewFacade view,
            @NonNull ExpressionResolver resolver
    ) {
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
                VariableMutationHandler.setVariable(div2View, name, value, resolver);
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

            return div2View.applyVideoCommand(id, command, resolver);
        } else if (DivItemChangeActionHandler.canHandle(action)) {
            return DivItemChangeActionHandler.handleAction(uri, view, resolver);
        } else if (StoredValuesActionHandler.canHandle(action)) {
            return StoredValuesActionHandler.handleAction(uri, view);
        }

        return false;
    }

    @Nullable
    private static ExpressionResolver findExpressionResolverById(Div2View divView, @Nullable String id) {
        if (id == null) {
            return null;
        }

        View targetView = ViewLocator.findSingleViewWithTag(divView, id);
        if (targetView instanceof DivHolderView) {
            BindingContext bindingContext = ((DivHolderView<?>) targetView).getBindingContext();
            if (bindingContext != null) {
                return bindingContext.getExpressionResolver();
            }
        }
        return null;
    }
}
