package com.yandex.divkit.demo.regression

import android.net.Uri
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div2.DivData
import com.yandex.divkit.demo.screenshot.DivAssetReader
import org.json.JSONObject

internal const val DIV_DEMO_ACTION_SCHEME = "div-demo-action"
private const val SET_DATA_HOST = "set_data"
private const val PATH_PARAM = "path"

internal data class ScenarioJson(
    val templatesJson: JSONObject?,
    val cardJson: JSONObject,
)

internal fun DivAssetReader.readScenarioJson(scenarioPath: String): ScenarioJson {
    val divJson = read(scenarioPath)
    return ScenarioJson(
        templatesJson = divJson.optJSONObject("templates"),
        cardJson = divJson.getJSONObject("card"),
    )
}

internal fun parseDivData(templatesJson: JSONObject?, cardJson: JSONObject): DivData {
    val environment = DivParsingEnvironment(ParsingErrorLogger.LOG).apply {
        if (templatesJson != null) parseTemplates(templatesJson)
    }
    return DivData(environment, cardJson)
}

internal fun parseSetDataPath(url: Uri): String? {
    if (url.host != SET_DATA_HOST) return null
    val assetName = url.getQueryParameter(PATH_PARAM) ?: return null
    return "regression_test_data/$assetName"
}
