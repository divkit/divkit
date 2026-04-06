package com.yandex.div.core;

import androidx.annotation.Px;

/**
 * Configuration for DivView
 */
public interface DivViewConfig {
    DivViewConfig DEFAULT = () -> true;

    boolean isContextMenuEnabled();

    @Px
    default int getLogCardScrollSignificantThreshold() {
        return 0;
    }
}
