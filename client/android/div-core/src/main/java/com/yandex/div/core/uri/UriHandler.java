package com.yandex.div.core.uri;

import android.net.Uri;
import androidx.annotation.NonNull;

/**
 * Basic interface for handling {@link Uri}'s.
 */
@Deprecated
public interface UriHandler {

    /**
     * Handles the given Uri.
     *
     * @param uri {@link Uri} to handle
     * @return {@code true} if the Uri was successfully handled, {@code false} otherwise.
     */
    boolean handleUri(@NonNull Uri uri);
}
