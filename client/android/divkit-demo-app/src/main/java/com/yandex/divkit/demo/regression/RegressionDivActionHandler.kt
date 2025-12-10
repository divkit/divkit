package com.yandex.divkit.demo.regression

import android.net.Uri
import com.yandex.div.DivDataTag
import com.yandex.div.core.DivViewFacade
import com.yandex.div.core.view2.Div2View
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAction
import com.yandex.div2.DivData
import com.yandex.divkit.demo.div.DemoDivActionHandler
import com.yandex.divkit.demo.div.parseToDiv2
import com.yandex.divkit.demo.screenshot.DivAssetReader
import com.yandex.divkit.demo.utils.DivkitDemoUriHandler
import com.yandex.divkit.demo.utils.setDataByConfig
import java.util.UUID

private const val DIV_DEMO_ACTION_SCHEME = "div-demo-action"
private const val SET_DATA_HOST = "set_data"
private const val PATH_PARAM = "path"

class RegressionDivActionHandler(
    uriHandlerDivkit: DivkitDemoUriHandler,
    private val divAssetReader: DivAssetReader
) : DemoDivActionHandler(uriHandlerDivkit) {
    override fun handleAction(action: DivAction, view: DivViewFacade, resolver: ExpressionResolver): Boolean {
        val url = action.url?.evaluate(resolver)
            ?: return super.handleAction(action, view, resolver)
        if (url.scheme == DIV_DEMO_ACTION_SCHEME) {
            handleDemoActionUrl(url, view)
            return true
        }
        return super.handleAction(action, view, resolver)
    }

    private fun handleDemoActionUrl(url: Uri, view: DivViewFacade) {
        if (url.host == SET_DATA_HOST) {
            val assetName = url.getQueryParameter(PATH_PARAM)
            if (assetName != null && view is Div2View) {
                val divData = divAssetReader
                    .read("regression_test_data/$assetName")
                    .parseToDiv2()
                setDivData(view, divData)
            }
        }
    }

    private fun setDivData(view: Div2View, divData: DivData) {
        val dataTag = view.dataTag.takeIf { it != DivDataTag.INVALID }
            ?: DivDataTag(UUID.randomUUID().toString())
        view.setDataByConfig(divData, dataTag, null)
    }
}
