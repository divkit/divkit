package com.yandex.div.core;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yandex.div.core.view2.Div2View;
import com.yandex.div2.DivTooltip;

/**
 * Interface to show tooltips with host's logic.
 */
public interface DivTooltipRestrictor {
    /**
     * Allows all tooltips.
     */
    DivTooltipRestrictor STUB = ((div2View, anchor, tooltip, multiple) -> true);

    /**
     * Called when handling div-action://show_tooltip
     *
     * @deprecated Use {@link DivTooltipRestrictor#canShowTooltip(Div2View, View, DivTooltip, boolean, String)}
     */
    @Deprecated
    boolean canShowTooltip(@NonNull Div2View div2View, @NonNull View anchor, @NonNull DivTooltip tooltip, boolean multiple);

    /**
     * Called when handling div-action://show_tooltip
     */
    default boolean canShowTooltip(
            @NonNull Div2View div2View,
            @NonNull View anchor,
            @NonNull DivTooltip tooltip,
            boolean multiple,
            @Nullable String scopeId
    ) {
        //noinspection deprecation
        return canShowTooltip(div2View, anchor, tooltip, multiple);
    }

    @Nullable
    default DivTooltipShownCallback getTooltipShownCallback() {
        return null;
    }

    /**
     * Notifies host about shown tooltip.
     */
    interface DivTooltipShownCallback {

        /**
         * Called when tooltip becomes visible to user.
         */
        void onDivTooltipShown(@NonNull Div2View div2View, @NonNull View anchor, @NonNull DivTooltip tooltip);

        /**
         * Called when tooltip is dismissed.
         */
        void onDivTooltipDismissed(@NonNull Div2View div2View, @NonNull View anchor, @NonNull DivTooltip tooltip);
    }
}
