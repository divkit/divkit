package com.yandex.divkit.demo.div

import com.yandex.div.core.downloader.DivDownloader
import com.yandex.div.core.downloader.DivPatchDownloadCallback
import com.yandex.div.core.images.LoadReference
import com.yandex.div.core.view2.Div2View
import com.yandex.div.network.DefaultDivDownloader
import com.yandex.div2.DivPatch
import com.yandex.divkit.demo.utils.DownloadList
import okhttp3.OkHttpClient

class DemoDivDownloaderWrapper(client: OkHttpClient) : DivDownloader {

    private val downloader = DefaultDivDownloader(client)
    private val downloads = DownloadList<DivPatchDownloadCallback>()

    override fun downloadPatch(
        divView: Div2View,
        downloadUrl: String,
        callback: DivPatchDownloadCallback
    ): LoadReference {
        downloads.add(callback)
        val loadReference = downloader.downloadPatch(divView, downloadUrl, CallbackWrapper(callback))
        return LoadReference {
            loadReference.cancel()
            downloads.remove(callback)
        }
    }

    val isIdle get() = downloads.size == 0

    fun resetIdle() = downloads.clean()

    private inner class CallbackWrapper(private val callback: DivPatchDownloadCallback) : DivPatchDownloadCallback {

        override fun onSuccess(patch: DivPatch) {
            downloads.remove(callback)
            callback.onSuccess(patch)
        }

        override fun onFail() {
            downloads.remove(callback)
            callback.onFail()
        }
    }
}
