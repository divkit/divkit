package com.yandex.div.legacy;

import androidx.annotation.Px;
import com.yandex.alicekit.core.annotations.PublicApi;

/**
 * Configuration for DivView
 */
@PublicApi
public interface LegacyDivViewConfig {
    LegacyDivViewConfig DEFAULT = () -> true;

    boolean isContextMenuEnabled();

    @Px
    default int getLogCardScrollSignificantThreshold() {
        return 0;
    }
}
