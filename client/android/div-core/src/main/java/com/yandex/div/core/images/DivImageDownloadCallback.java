package com.yandex.div.core.images;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import com.yandex.div.core.annotations.PublicApi;

@PublicApi
public class DivImageDownloadCallback {

    /**
     * Called when image is successfully loaded.
     */
    @UiThread
    public void onSuccess(@NonNull CachedBitmap cachedBitmap) {
        // no implementation
    }

    /**
     * Called when image is successfully loaded in a form of drawable
     * Use when you want to draw Drawable object directly
     * Ex.: Coil GIFs
     */
    @UiThread
    public void onSuccess(@NonNull Drawable drawable) {
        // no implementation
    }

    /**
     * Called when image is successfully loaded in a form of picture drawable
     * Use when you want to draw PictureDrawable object directly
     * Ex.: Vector images in SVG Image Loader
     */
    @UiThread
    public void onSuccess(@NonNull PictureDrawable pictureDrawable) {
        // no implementation
    }

    /**
     * Is called when image load is failed.
     */
    @UiThread
    public void onError() {
        // no implementation
    }

    /**
     * Is called if immediate call to {@link #onError} or {@link #onSuccess(CachedBitmap)} didn't happen.
     * That is, image is not in cache or it's only on disk cache and callback asked for memory cache only.
     * Callback will be called later when result is ready.
     *
     * It is either executed immediately or never executed.
     */
    @UiThread
    public void onScheduling() {
        // no implementation
    }

    /**
     * May be used to provide additional data for image load failures logging.
     */
    @UiThread
    @Nullable
    public String getAdditionalLogInfo() {
        return null;
    }
}
