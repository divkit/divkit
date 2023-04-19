package com.yandex.div.core;

import android.view.View;
import android.widget.Space;
import androidx.annotation.NonNull;
import com.yandex.div.core.view2.Div2View;
import com.yandex.div2.DivCustom;

/**
 * Used to implement host-specific views
 * @deprecated use {@link DivCustomContainerChildFactory}.
 */
@Deprecated
public interface DivCustomViewFactory {

    DivCustomViewFactory STUB = (data, divView, listener) -> new Space(divView.getContext());

    void create(@NonNull DivCustom data, @NonNull Div2View divView, @NonNull OnViewCreatedListener listener);

    interface OnViewCreatedListener {
        void onCreate(@NonNull View view);
    }
}


