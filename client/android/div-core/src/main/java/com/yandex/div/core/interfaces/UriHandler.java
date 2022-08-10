package com.yandex.div.core.interfaces;

import android.net.Uri;
import androidx.annotation.NonNull;
import com.yandex.div.core.annotations.PublicApi;

/**
 * Basic interface for handling {@link Uri}'s.
 */
@PublicApi
public interface UriHandler {

    /**
     * Handles the given Uri.
     *
     * @param uri {@link Uri} to handle
     * @return {@code true} if the Uri was successfully handled, {@code false} otherwise.
     */
    boolean handleUri(@NonNull Uri uri);
}
