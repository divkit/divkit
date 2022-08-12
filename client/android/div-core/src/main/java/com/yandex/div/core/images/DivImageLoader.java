package com.yandex.div.core.images;

import android.widget.ImageView;
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
    @NonNull
    LoadReference loadImage(@NonNull String imageUrl, @NonNull DivImageDownloadCallback callback);

    /**
     * Starts image loading by given <code>imageUrl</code>. Download raw bytes in result.
     * <p>
     * Contract : <code>callback</code> MUST BE stored in {@link java.lang.ref.WeakReference} in order to prevent leakage.
     *
     * @param imageUrl image url.
     * @param callback callback to invoke after image is loaded.
     * @return reference to cancel loading
     */
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
    @NonNull
    default LoadReference loadImageBytes(@NonNull String imageUrl, @NonNull DivImageDownloadCallback callback, @DivImagePriority int loadPriority) {
        return loadImageBytes(imageUrl, callback);
    }
}
