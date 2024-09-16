package com.yandex.div.core.downloader

import android.net.Uri
import com.yandex.div.core.DivActionHandler.DivActionReason
import com.yandex.div.core.DivViewFacade
import com.yandex.div.core.view2.Div2View
import com.yandex.div.internal.Assert
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAction
import com.yandex.div2.DivActionDownload
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
    fun handleAction(action: DivAction, view: Div2View, resolver: ExpressionResolver): Boolean {
        val uri = action.url?.evaluate(resolver) ?: return false
        return handleAction(uri, action.downloadCallbacks, view, resolver)
    }

    @JvmStatic
    fun handleVisibilityAction(action: DivSightAction, view: Div2View, resolver: ExpressionResolver): Boolean {
        val uri = action.url?.evaluate(resolver) ?: return false
        return handleAction(uri, action.downloadCallbacks, view, resolver)
    }

    fun handleAction(action: DivActionDownload, callbacks: DivDownloadCallbacks?, view: Div2View, resolver: ExpressionResolver): Boolean {
        val url = action.url.evaluate(resolver)
        val actualOnFail = action.onFailActions ?: callbacks?.onFailActions
        val actualOnSuccess = action.onSuccessActions ?: callbacks?.onSuccessActions
        return executeDownload(url, actualOnSuccess, actualOnFail, view, resolver)
    }

    private fun handleAction(
        uri: Uri,
        downloadCallbacks: DivDownloadCallbacks?,
        view: Div2View,
        resolver: ExpressionResolver,
    ): Boolean {
        val downloadUrl = uri.getQueryParameter(PARAM_URL) ?: return false
        return executeDownload(downloadUrl, downloadCallbacks?.onSuccessActions, downloadCallbacks?.onFailActions, view, resolver)
    }

    private fun executeDownload(
        downloadUrl: String,
        onSuccessActions: List<DivAction>?,
        onFailActions: List<DivAction>?,
        view: Div2View,
        resolver: ExpressionResolver
    ): Boolean {
        val callback = object : DivPatchDownloadCallback {
            override fun onSuccess(patch: DivPatch) {
                val success = view.applyPatch(patch)
                if (success) {
                    view.bulkActions {
                        onSuccessActions?.forEach {
                            view.handleAction(it, DivActionReason.PATCH, resolver)
                        }
                    }
                }
            }

            override fun onFail() {
                view.bulkActions {
                    onFailActions?.forEach {
                        view.handleAction(it, DivActionReason.PATCH, resolver)
                    }
                }
            }
        }
        val loadRef = view.div2Component.divDownloader.downloadPatch(view, downloadUrl, callback)
        view.addLoadReference(loadRef, view)
        return true
    }
}
