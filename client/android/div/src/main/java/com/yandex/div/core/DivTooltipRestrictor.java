package com.yandex.div.core;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yandex.div.core.annotations.PublicApi;
import com.yandex.div.core.view2.Div2View;
import com.yandex.div2.DivTooltip;

/**
 * Interface to show tooltips with host's logic.
 */
@PublicApi
public interface DivTooltipRestrictor {
    /**
     * Allows all tooltips.
     */
    DivTooltipRestrictor STUB = ((anchor, tooltip) -> true);

    /**
     * Called when handling div-action://show_tooltip
     */
    @Deprecated
    boolean canShowTooltip(@NonNull View anchor, @NonNull DivTooltip tooltip);

    /**
     * Called when handling div-action://show_tooltip
     */
    default boolean canShowTooltip(@NonNull Div2View div2View, @NonNull View anchor, @NonNull DivTooltip tooltip) {
        return canShowTooltip(anchor, tooltip);
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
        @Deprecated
        default void onDivTooltipShown(@NonNull View anchor, @NonNull DivTooltip tooltip) {}

        /**
         * Called when tooltip becomes visible to user.
         */
        default void onDivTooltipShown(@NonNull Div2View div2View, @NonNull View anchor, @NonNull DivTooltip tooltip) {
            onDivTooltipShown(anchor, tooltip);
        }

        /**
         * Called when tooltip is dismissed.
         */
        @Deprecated
        default void onDivTooltipDismissed(@NonNull View anchor, @NonNull DivTooltip tooltip) {}

        /**
         * Called when tooltip is dismissed.
         */
        default void onDivTooltipDismissed(@NonNull Div2View div2View,
                                           @NonNull View anchor,
                                           @NonNull DivTooltip tooltip) {
            onDivTooltipDismissed(anchor, tooltip);
        }
    }
}
