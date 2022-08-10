package com.yandex.divkit.demo.regression

import android.content.Context
import android.net.Uri
import com.yandex.div.DivDataTag
import com.yandex.div.core.DivViewFacade
import com.yandex.div.core.view2.Div2View
import com.yandex.div2.DivAction
import com.yandex.div2.DivData
import com.yandex.divkit.demo.div.DemoDivActionHandler
import com.yandex.divkit.demo.div.parseToDiv2
import com.yandex.divkit.demo.screenshot.DivAssetReader
import com.yandex.divkit.regression.di.provideRegressionConfig
import com.yandex.divkit.demo.utils.DivkitDemoUriHandler
import java.util.UUID

private const val DIV_DEMO_ACTION_SCHEME = "div-demo-action"
private const val SET_DATA_HOST = "set_data"
private const val AUTHORITY_RECORD_SCREEN = "record_screen"
private const val PATH_PARAM = "path"
private const val VALUE_PARAM = "value"

class RegressionDivActionHandler(
    uriHandlerDivkit: DivkitDemoUriHandler,
    private val divAssetReader: DivAssetReader,
    private val context: Context,
) : DemoDivActionHandler(uriHandlerDivkit) {
    override fun handleAction(action: DivAction, view: DivViewFacade): Boolean {
        val url = action.url?.evaluate(view.expressionResolver) ?: return false
        if (url.scheme == DIV_DEMO_ACTION_SCHEME) {
            if (handleScreenRecordActionUrl(url, view)) return true
            handleDemoActionUrl(url, view)
            return true
        }
        return super.handleAction(action, view)
    }

    private fun handleDemoActionUrl(url: Uri, view: DivViewFacade) {
        if (url.host == SET_DATA_HOST) {
            val assetName = url.getQueryParameter(PATH_PARAM)
            if (assetName != null && view is Div2View) {
                val divData = divAssetReader.read(assetName).parseToDiv2()
                setDivData(view, divData)
            }
        }
    }

    private fun handleScreenRecordActionUrl(uri: Uri, view: DivViewFacade): Boolean {
        if (uri.authority != AUTHORITY_RECORD_SCREEN || uri.scheme != DIV_DEMO_ACTION_SCHEME)
            return false

        val regressionConfig by lazy(LazyThreadSafetyMode.NONE) { context.provideRegressionConfig() }
        val value = uri.getQueryParameter(VALUE_PARAM)?.toIntOrNull() ?: return false
        regressionConfig.isRecordScreenEnabled = value > 0
        return true
    }

    private fun setDivData(view: Div2View, divData: DivData) {
        val dataTag = view.dataTag.takeIf { it != DivDataTag.INVALID }
            ?: DivDataTag(UUID.randomUUID().toString())
        view.setData(divData, dataTag)
    }
}
