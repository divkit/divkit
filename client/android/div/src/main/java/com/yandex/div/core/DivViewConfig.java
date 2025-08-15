package com.yandex.div.core;

import androidx.annotation.Px;
import com.yandex.div.core.annotations.PublicApi;

/**
 * Configuration for DivView
 */
@PublicApi
public interface DivViewConfig {
    DivViewConfig DEFAULT = () -> true;

    boolean isContextMenuEnabled();

    @Px
    default int getLogCardScrollSignificantThreshold() {
        return 0;
    }
}
