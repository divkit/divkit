package com.yandex.div.core;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import com.yandex.div.core.annotations.PublicApi;

/**
 * Provides stubs for images in div2 cards.
 */
@PublicApi
public interface Div2ImageStubProvider {
    Div2ImageStubProvider STUB = ColorDrawable::new;

    /**
     * @param color preferred background color for stub
     * @return drawable or null if stub should not be used
     */
    @Nullable
    Drawable getImageStubDrawable(@ColorInt int color);
}
