package com.yandex.div.core.downloader;

import androidx.annotation.NonNull;
import com.yandex.div.core.annotations.PublicApi;
import com.yandex.div.core.images.LoadReference;
import com.yandex.div.core.view2.Div2View;

/**
 * Downloads patches for Divs
 * A default implementation, [DefaultDivDownloader], is provided.
 */
@PublicApi
public interface DivDownloader {

    DivDownloader STUB = (divView, downloadUrl, callback) -> {
        throw new AssertionError("To load patch you must provide implementation of " +
          "DivDownloader to your DivConfiguration. ");
    };

    /**
     * Starts loading py patch by {@param downloadUrl}
     * @param divView
     * @param downloadUrl
     * @param callback
     */
    LoadReference downloadPatch(@NonNull Div2View divView,
                       @NonNull String downloadUrl,
                       @NonNull DivPatchDownloadCallback callback);
}
