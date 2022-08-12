package com.yandex.div.legacy;

import android.net.Uri;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yandex.alicekit.core.annotations.PublicApi;

/**
 * Contains methods for SearchApp automatic logging.
 */
@PublicApi
public interface DivAutoLogger {

    DivAutoLogger DEFAULT = new DivAutoLogger() {

        @Override
        public void setId(@NonNull View view, @NonNull String id) {
        }

        @Override
        public void logPopupMenuItemClick(@NonNull View view, int position, @Nullable String text) {
        }
    };

    void setId(@NonNull View view, @NonNull String id);

    void logPopupMenuItemClick(@NonNull View view, int position, @Nullable String text);

    default void logPopupMenuItemClick(@NonNull View view, int position, @Nullable String text, @Nullable Uri url) {
        logPopupMenuItemClick(view, position, text);
    }
}
