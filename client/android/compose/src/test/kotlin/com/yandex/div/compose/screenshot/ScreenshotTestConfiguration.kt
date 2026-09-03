package com.yandex.div.compose.screenshot

import androidx.compose.ui.unit.LayoutDirection
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div2.DivData
import org.json.JSONObject
import kotlin.test.fail

class ScreenshotTestConfiguration(
    val name: String,
    private val json: JSONObject
) {
    private val configuration = json.optJSONObject("configuration")

    val failOnParsingError = configuration?.optBoolean("fail_on_parsing_error", true) ?: true

    val layoutDirection: LayoutDirection
        get() {
            return if (configuration?.optString("layout_direction") == "rtl") {
                LayoutDirection.Rtl
            } else {
                LayoutDirection.Ltr
            }
        }

    fun parseDivData(): DivData {
        return DivData(
            DivParsingEnvironment(
                logger = {
                    if (failOnParsingError) {
                        fail(it.message)
                    }
                }
            ).apply {
                json.optJSONObject("templates")?.let {
                    parseTemplates(it)
                }
            },
            json.getJSONObject("card")
        )
    }

    override fun toString() = name
}
