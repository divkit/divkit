package com.yandex.div.view.pooling;

import android.view.View;
import androidx.annotation.NonNull;

public interface ViewPool {

    <T extends View> void register(@NonNull String tag, @NonNull ViewFactory<T> factory, int capacity);

    void unregister(@NonNull String tag);

    @NonNull
    <T extends View> T obtain(@NonNull String tag);
}
