package com.yandex.div.legacy.state;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import com.yandex.alicekit.core.annotations.PublicApi;
import javax.annotation.Nullable;

/**
 * Storage of activated states that are keyed by pair of cardId and activation path.
 */
@PublicApi
public interface LegacyDivStateCache {
    @MainThread
    void putState(@NonNull String cardId, @NonNull String path, @NonNull String state);

    @MainThread
    void putRootState(@NonNull String cardId, @NonNull String state);

    @MainThread
    @Nullable
    String getState(@NonNull String cardId, @NonNull String path);

    @MainThread
    @Nullable
    String getRootState(@NonNull String cardId);
}
