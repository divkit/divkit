package com.yandex.div.core.downloader

import android.net.Uri
import com.yandex.div.core.DivActionHandler.DivActionReason
import com.yandex.div.core.DivViewFacade
import com.yandex.div.core.view2.Div2View
import com.yandex.div.internal.Assert
import com.yandex.div2.DivAction
import com.yandex.div2.DivDownloadCallbacks
import com.yandex.div2.DivPatch
import com.yandex.div2.DivSightAction

private const val PARAM_URL = "url"
private const val AUTHORITY_DOWNLOAD = "download"

internal object DivDownloadActionHandler {

    @JvmStatic
    fun canHandle(uri: Uri?, divViewFacade: DivViewFacade): Boolean {
        val authority = uri?.authority ?: return false
        if (AUTHORITY_DOWNLOAD == authority) {
            val url = uri.getQueryParameter(PARAM_URL)
            if (url == null) {
                Assert.fail("$PARAM_URL param is required!")
                return false
            }
            if (divViewFacade !is Div2View) {
                Assert.fail("Div2View should be used!")
                return false
            }
            return true
        }
        return false
    }

    @JvmStatic
    fun handleAction(action: DivAction, view: Div2View): Boolean {
        val uri = action.url?.evaluate(view.expressionResolver) ?: return false
        return handleAction(uri, action.downloadCallbacks, view)
    }

    @JvmStatic
    fun handleVisibilityAction(action: DivSightAction, view: Div2View): Boolean {
        val uri = action.url?.evaluate(view.expressionResolver) ?: return false
        return handleAction(uri, action.downloadCallbacks, view)
    }

    private fun handleAction(uri: Uri, downloadCallbacks: DivDownloadCallbacks?, view: Div2View): Boolean {
        val downloadUrl = uri.getQueryParameter(PARAM_URL) ?: return false
        val callback = object : DivPatchDownloadCallback {
            override fun onSuccess(patch: DivPatch) {
                val success = view.applyPatch(patch)
                if (success) {
                    view.bulkActions {
                        downloadCallbacks?.onSuccessActions?.forEach { view.handleAction(it, DivActionReason.PATCH) }
                    }
                }
            }

            override fun onFail() {
                view.bulkActions {
                    downloadCallbacks?.onFailActions?.forEach { view.handleAction(it, DivActionReason.PATCH) }
                }
            }
        }
        val loadRef = view.div2Component.divDownloader.downloadPatch(view, downloadUrl, callback)
        view.addLoadReference(loadRef, view)
        return true
    }
}
