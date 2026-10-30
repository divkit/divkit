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

    val steps by lazy { InteractiveScreenshotTestData.parse(json).steps }

    /** Runtime errors the scenario reports on purpose; null when the first runtime error fails it. */
    val runtimeErrorCount: Int? = json.optJSONObject("configuration")
        ?.takeIf { it.has("runtime_error_count") }
        ?.getInt("runtime_error_count")

    override fun toString() = baseConfiguration.name
}
