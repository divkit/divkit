package com.yandex.div.core.images;

import android.widget.ImageView;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

/**
 * Image loader contract.
 */
public interface DivImageLoader {

    /**
     * Starts image loading by given <code>imageUrl</code>.
     * <p>
     * Contract : <code>callback</code> MUST BE stored in {@link java.lang.ref.WeakReference} in order to prevent leakage.
     *
     * @param imageUrl image url.
     * @param callback callback to invoke after image is loaded.
     * @return reference to cancel loading
     */
    @MainThread
    @NonNull
    LoadReference loadImage(@NonNull String imageUrl, @NonNull DivImageDownloadCallback callback);

    /**
     * @deprecated Support of this property will be removed.
     * It's loader's responsibility to load images in {@code .svg} format.
     * If loader doesn't support it, image will not be loaded.
     * <p>
     * Property indicating if the image loader can handle svg.
     * False if not overridden.
     *
     * @return true if image loader supports svg.
     */
    @Deprecated
    default Boolean hasSvgSupport() {
        return false;
    }

    /**
     * Property indicating if the image bitmap size can be limited after downloading by this loader.
     * True if not overridden.
     *
     * @return true if need to limit image bitmap size
     */
    default boolean needLimitBitmapSize() {
        return true;
    }

    /**
     * @deprecated Use {@link #loadAnimatedImage(String, DivImageDownloadCallback)}.
     * <p>
     * Starts image loading by given <code>imageUrl</code>. Download raw bytes in result.
     * <p>
     * Contract : <code>callback</code> MUST BE stored in {@link java.lang.ref.WeakReference} in order to prevent leakage.
     *
     * @param imageUrl image url.
     * @param callback callback to invoke after image is loaded.
     * @return reference to cancel loading
     */
    @MainThread
    @NonNull
    @Deprecated
    default LoadReference loadImageBytes(@NonNull String imageUrl, @NonNull DivImageDownloadCallback callback) {
        return loadImage(imageUrl, callback);
    }

    /**
     * Starts image loading by given {@code imageUrl}.
     * Expecting to download {@link android.graphics.drawable.AnimatedImageDrawable}.
     * <p>
     * Contract : {@code callback} MUST BE stored in {@link java.lang.ref.WeakReference} in order to prevent leakage.
     *
     * @param imageUrl image url.
     * @param callback callback to invoke after image is loaded.
     * @return reference to cancel loading
     */
    @MainThread
    @NonNull
    default LoadReference loadAnimatedImage(@NonNull String imageUrl, @NonNull DivImageDownloadCallback callback) {
        return loadImageBytes(imageUrl, callback);
    }

    /**
     * @deprecated This method is not used in DivKit.
     * <p>
     * Starts image loading reference by given <code>imageUrl</code>.
     * <p>
     *
     * @param imageUrl image url.
     * @param imageView image view to set image bitmap to.
     * @return reference to cancel loading
     */
    @MainThread
    @NonNull
    @Deprecated
    default LoadReference loadImage(@NonNull String imageUrl, @NonNull ImageView imageView) {
        return () -> {};
    }

    /**
     * Starts image loading by given <code>imageUrl</code>.
     * <p>
     * Contract : <code>callback</code> MUST BE stored in {@link java.lang.ref.WeakReference} in order to prevent leakage.
     *
     * @param imageUrl image url.
     * @param callback callback to invoke after image is loaded.
     * @param loadPriority loading priority.
     * @return reference to cancel loading
     */
    @MainThread
    @NonNull
    default LoadReference loadImage(@NonNull String imageUrl, @NonNull DivImageDownloadCallback callback, @DivImagePriority int loadPriority) {
        return loadImage(imageUrl, callback);
    }

    /**
     * @deprecated Use {@link #loadAnimatedImage(String, DivImageDownloadCallback, int)}.
     * <p>
     * Starts image loading by given <code>imageUrl</code>. Download raw bytes in result.
     * <p>
     * Contract : <code>callback</code> MUST BE stored in {@link java.lang.ref.WeakReference} in order to prevent leakage.
     *
     * @param imageUrl image url.
     * @param callback callback to invoke after image is loaded.
     * @param loadPriority loading priority.
     * @return reference to cancel loading
     */
    @MainThread
    @NonNull
    @Deprecated
    default LoadReference loadImageBytes(@NonNull String imageUrl, @NonNull DivImageDownloadCallback callback, @DivImagePriority int loadPriority) {
        return loadImageBytes(imageUrl, callback);
    }

    /**
     * Starts image loading by given {@code imageUrl}.
     * Expecting to download {@link android.graphics.drawable.AnimatedImageDrawable}.
     * <p>
     * Contract : {@code callback} MUST BE stored in {@link java.lang.ref.WeakReference} in order to prevent leakage.
     *
     * @param imageUrl image url.
     * @param callback callback to invoke after image is loaded.
     * @param loadPriority loading priority.
     * @return reference to cancel loading
     */
    @MainThread
    @NonNull
    default LoadReference loadAnimatedImage(
            @NonNull String imageUrl,
            @NonNull DivImageDownloadCallback callback,
            @DivImagePriority int loadPriority
    ) {
        return loadAnimatedImage(imageUrl, callback);
    }
}
