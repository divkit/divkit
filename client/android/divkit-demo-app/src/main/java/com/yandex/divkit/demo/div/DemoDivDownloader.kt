package com.yandex.divkit.demo.div

import com.yandex.div.core.downloader.DivDownloader
import com.yandex.div.core.downloader.DivPatchDownloadCallback
import com.yandex.div.core.images.LoadReference
import com.yandex.div.core.view2.Div2View
import com.yandex.divkit.demo.Container
import com.yandex.divkit.demo.utils.loadText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject

class DemoDivDownloader : DivDownloader {

    override fun downloadPatch(divView: Div2View, downloadUrl: String, callback: DivPatchDownloadCallback): LoadReference {
        val job = GlobalScope.launch(Dispatchers.Main) {
            val json = Container.httpClient.loadText(downloadUrl)
            if (json != null) {
                try {
                    callback.onSuccess(JSONObject(json).asDivPatchWithTemplates())
                } catch (e: JSONException) {
                    callback.onFail()
                }
            } else {
                callback.onFail()
            }
        }
        return LoadReference {
            job.cancel("cancel all downloads")
        }
    }
}
