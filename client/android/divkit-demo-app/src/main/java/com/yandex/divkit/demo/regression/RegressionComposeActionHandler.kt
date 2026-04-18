package com.yandex.divkit.demo.regression

import androidx.compose.runtime.MutableState
import com.yandex.div.compose.actions.DivActionData
import com.yandex.div.compose.actions.DivActionHandlingContext
import com.yandex.div.compose.actions.DivExternalActionHandler
import com.yandex.div2.DivData
import com.yandex.divkit.demo.screenshot.DivAssetReader

class RegressionComposeActionHandler(
    private val divAssetReader: DivAssetReader,
    private val divDataState: MutableState<DivData>,
) : DivExternalActionHandler {

    override fun handle(context: DivActionHandlingContext, action: DivActionData) {
        val url = action.url ?: return
        if (url.scheme != DIV_DEMO_ACTION_SCHEME) return
        val path = parseSetDataPath(url) ?: return
        val (templatesJson, cardJson) = divAssetReader.readScenarioJson(path)
        divDataState.value = parseDivData(templatesJson, cardJson)
    }
}
