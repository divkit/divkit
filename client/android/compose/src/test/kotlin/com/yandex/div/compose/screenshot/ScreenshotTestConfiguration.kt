package com.yandex.div.compose.screenshot

import androidx.compose.ui.unit.LayoutDirection
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div2.DivData
import org.json.JSONObject
import java.io.File
import kotlin.test.fail

class ScreenshotTestConfiguration(
    val data: DivData,
    val layoutDirection: LayoutDirection
) {

    companion object {
        fun parse(file: File): ScreenshotTestConfiguration {
            val json = JSONObject(file.readText())
            val configuration = json.optJSONObject("configuration")
            return ScreenshotTestConfiguration(
                data = DivData(
                    DivParsingEnvironment(
                        logger = { fail(it.message) }
                    ).apply {
                        json.optJSONObject("templates")?.let {
                            parseTemplates(it)
                        }
                    },
                    json.getJSONObject("card")
                ),
                layoutDirection = if (configuration?.optString("layout_direction") == "rtl") {
                    LayoutDirection.Rtl
                } else {
                    LayoutDirection.Ltr
                }
            )
        }
    }
}
