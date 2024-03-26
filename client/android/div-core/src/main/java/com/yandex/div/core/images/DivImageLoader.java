package com.yandex.div.core.images;

import android.widget.ImageView;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

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
     * Starts image loading with a given <code>imageUrl</code>.
     * If not overridden, works as loadImage(imageUrl, callback)
     * <p>
     * Contract : <code>callback</code> MUST BE stored in {@link java.lang.ref.WeakReference} in order to prevent leakage.
     *
     * @param imageUrl image url.
     * @param shouldBeRasterized property indicating if the vector image should be rasterized
     * @param callback callback to invoke after image is loaded.
     * @return reference to cancel loading
     */
    @MainThread
    @NonNull
    default LoadReference loadImage(@NonNull String imageUrl, @NotNull boolean shouldBeRasterized, @NonNull DivImageDownloadCallback callback) {
        return loadImage(imageUrl, callback);
    }

    /**
     * Property indicating if the image loader can handle svg.
     * False if not overridden.
     *
     * @return true if image loader supports svg.
     */
    default Boolean doesSupportSvg() {
        return false;
    }

    /**
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
    LoadReference loadImageBytes(@NonNull String imageUrl, @NonNull DivImageDownloadCallback callback);

    /**
     * Starts image loading reference by given <code>imageUrl</code>.
     * <p>
     *
     * @param imageUrl image url.
     * @param imageView image view to set image bitmap to.
     * @return reference to cancel loading
     */
    @MainThread
    @NonNull
    LoadReference loadImage(@NonNull String imageUrl, @NonNull ImageView imageView);

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
    default LoadReference loadImageBytes(@NonNull String imageUrl, @NonNull DivImageDownloadCallback callback, @DivImagePriority int loadPriority) {
        return loadImageBytes(imageUrl, callback);
    }
}
