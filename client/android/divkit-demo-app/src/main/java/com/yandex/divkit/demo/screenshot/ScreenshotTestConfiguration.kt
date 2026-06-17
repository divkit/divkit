package com.yandex.divkit.demo.screenshot

import android.view.View
import org.json.JSONObject

internal class ScreenshotTestConfiguration private constructor(
    private val isRtl: Boolean,
) {

    fun applyTo(view: View) {
        if (isRtl) {
            view.layoutDirection = View.LAYOUT_DIRECTION_RTL
        }
    }

    companion object {

        private const val KEY_CONFIGURATION = "configuration"
        private const val KEY_LAYOUT_DIRECTION = "layout_direction"
        private const val LAYOUT_DIRECTION_RTL = "rtl"

        fun from(testCaseJson: JSONObject): ScreenshotTestConfiguration {
            val configuration = testCaseJson.optJSONObject(KEY_CONFIGURATION)
            return ScreenshotTestConfiguration(
                isRtl = configuration?.optString(KEY_LAYOUT_DIRECTION) == LAYOUT_DIRECTION_RTL,
            )
        }
    }
}
