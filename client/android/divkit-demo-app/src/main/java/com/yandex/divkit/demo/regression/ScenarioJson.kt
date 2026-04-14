package com.yandex.divkit.demo.regression

import com.yandex.divkit.demo.screenshot.DivAssetReader
import org.json.JSONObject

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
