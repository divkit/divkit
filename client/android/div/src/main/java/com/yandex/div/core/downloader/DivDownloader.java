package com.yandex.div.core.downloader;

import androidx.annotation.NonNull;
import com.yandex.div.core.annotations.PublicApi;
import com.yandex.div.core.images.LoadReference;
import com.yandex.div.core.view2.Div2View;

/**
 * Downloads patches for Divs
 */
@PublicApi
public interface DivDownloader {

    DivDownloader STUB = (divView, downloadUrl, callback) -> {
        return new LoadReference() {
                @Override
                public void cancel() { /* stub */ }
            };
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
