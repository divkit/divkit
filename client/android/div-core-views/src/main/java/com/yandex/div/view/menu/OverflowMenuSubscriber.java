package com.yandex.div.view.menu;

import androidx.annotation.NonNull;

public interface OverflowMenuSubscriber {
    /**
     * Subscribes given listener for further callbacks invocation.
     *
     * @param listener listener
     */
    void subscribe(@NonNull Listener listener);

    interface Listener {
        /**
         * Dismisses {@link OverflowMenuWrapper overflow menu's} popup.
         */
        void dismiss();
    }
}
