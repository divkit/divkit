package com.yandex.div.core;

import android.net.Uri;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @deprecated backward compat call. To be deleted VERY soon
 */
@Deprecated
public interface DivAutoLogger {

    DivAutoLogger DEFAULT = new DivAutoLogger() {

        @Override
        public void setId(@NonNull View view, @NonNull String id) {
        }

        @Override
        public void logPopupMenuItemClick(@NonNull View view, int position, @Nullable String text) {
        }
    };

    default void setId(@NonNull View view, @NonNull String id) {
    }

    default void logPopupMenuItemClick(@NonNull View view, int position, @Nullable String text) {
    }

    default void logPopupMenuItemClick(@NonNull View view, int position, @Nullable String text, @Nullable Uri url) {
        logPopupMenuItemClick(view, position, text);
    }
}
