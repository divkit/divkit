package com.yandex.div.legacy;

import android.net.Uri;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yandex.alicekit.core.annotations.PublicApi;
import com.yandex.alicekit.core.utils.Assert;
import com.yandex.div.json.expressions.ExpressionResolver;
import com.yandex.div.legacy.view.DivView;
import com.yandex.div2.DivAction;
import com.yandex.div2.DivVisibilityAction;
import org.json.JSONObject;

/**
 * Handles URIs and payloads, that are triggered by click events in DivView.
 */
@PublicApi
public class LegacyDivActionHandler {
    private static final String SCHEME_DIV_ACTION = "div-action";

    private static final String AUTHORITY_SWITCH_STATE = "set_state";

    private static final String PARAM_STATE_ID = "state_id";

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
    public boolean handleUri(@NonNull Uri uri, @NonNull DivView view) {
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
    public boolean handleAction(@NonNull DivAction action, @NonNull DivView view) {
        Uri url = action.url != null ? action.url.evaluate(ExpressionResolver.EMPTY) : null;
        return handleActionUrl(url, view);
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
    public boolean handleAction(@NonNull DivVisibilityAction action, @NonNull DivView view) {
        Uri url = action.url != null ? action.url.evaluate(ExpressionResolver.EMPTY) : null;
        return handleActionUrl(url, view);
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
    public final boolean handleActionUrl(@Nullable Uri uri, @NonNull DivView view) {
        if (uri == null) {
            return false;
        }

        //noinspection SimplifiableIfStatement
        if (SCHEME_DIV_ACTION.equals(uri.getScheme())) {
            return handleAction(uri, view);
        }

        return false;
    }

    private boolean handleAction(@NonNull Uri uri, @NonNull DivView view) {
        String action = uri.getAuthority();
        if (!AUTHORITY_SWITCH_STATE.equals(action)) {
            return false;
        }
        // legacy divs supports only uri like: div-action://set_state?state_id=1
        String stateId = uri.getQueryParameter(PARAM_STATE_ID);
        if (stateId == null) {
            Assert.fail(PARAM_STATE_ID + " param is required");
            return false;
        }
        try {
            int state = Integer.parseInt(stateId);
            view.switchToState(state);
        } catch (NumberFormatException e) {
            Assert.fail("Switch state action should contain integer stateId, but was:" + uri.toString(), e);
            return false;
        }
        return true;
    }
}
