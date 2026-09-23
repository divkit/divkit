package com.yandex.div.compose.screenshot

import com.yandex.div.test.crossplatform.InteractiveScreenshotTestData
import org.json.JSONObject

class InteractiveScreenshotTestConfiguration(
    name: String,
    private val json: JSONObject
) {
    val baseConfiguration = ScreenshotTestConfiguration(
        name = name,
        json = json.getJSONObject("div_data"),
        configurationJson = json.optJSONObject("configuration"),
    )

    val steps = InteractiveScreenshotTestData.parse(json).steps

    override fun toString() = baseConfiguration.name
}
