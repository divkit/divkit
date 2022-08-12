package com.yandex.div.core;

import androidx.annotation.NonNull;
import com.yandex.div.core.annotations.PublicApi;
import com.yandex.div.core.view2.Div2View;

@PublicApi
public interface DivStateChangeListener {

    DivStateChangeListener STUB = new DivStateChangeListener() {
        @Override
        public void onDivAnimatedStateChanged(@NonNull Div2View divView) { /* empty */ }
    };

    default void onDivAnimatedStateChanged(@NonNull Div2View divView) { /* empty */ }
}
