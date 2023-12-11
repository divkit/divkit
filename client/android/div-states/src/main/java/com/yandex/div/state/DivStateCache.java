package com.yandex.div.state;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yandex.div.core.annotations.PublicApi;

/**
 * Storage of activated states that are keyed by pair of cardId and activation path.
 */
@PublicApi
public interface DivStateCache {
    @AnyThread
    void putState(@NonNull String cardId, @NonNull String path, @NonNull String state);

    @AnyThread
    void putRootState(@NonNull String cardId, @NonNull String state);

    @AnyThread
    @Nullable
    String getState(@NonNull String cardId, @NonNull String path);

    @AnyThread
    @Nullable
    String getRootState(@NonNull String cardId);

    @AnyThread
    void clear();

    @AnyThread
    void resetCard(@NonNull String cardId);
}
