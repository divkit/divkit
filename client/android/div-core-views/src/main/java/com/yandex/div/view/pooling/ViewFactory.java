package com.yandex.div.view.pooling;

import android.view.View;
import androidx.annotation.NonNull;

public interface ViewFactory<T extends View> {

    @NonNull
    T createView();
}
