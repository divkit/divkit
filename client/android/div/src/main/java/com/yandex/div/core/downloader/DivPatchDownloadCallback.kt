package com.yandex.div.core.downloader

import androidx.annotation.MainThread
import com.yandex.div.core.annotations.PublicApi
import com.yandex.div2.DivPatch

/**
 * Div download callbacks.
 */
@PublicApi
interface DivPatchDownloadCallback {

    /**
     * Call when patch successfully loaded. {@link com.yandex.div2.DownloadCallbacks.onSuccessActions} will be called after.
     */
    @MainThread
    fun onSuccess(patch: DivPatch)

    /**
    * Call when patch failed to download. {@link com.yandex.div2.DownloadCallbacks.onFailActions} will be called after.
    */
    @MainThread
    fun onFail()
}
