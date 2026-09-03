package com.yandex.divkit.demo.screenshot

import android.view.View
import com.yandex.div.json.ParsingErrorLogger
import org.json.JSONObject

internal class ScreenshotTestConfiguration private constructor(
    private val isRtl: Boolean,
    val parsingErrorLogger: ParsingErrorLogger,
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
            val failOnParsingError = configuration
                ?.optBoolean("fail_on_parsing_error", true)
                ?: true
            return ScreenshotTestConfiguration(
                isRtl = configuration?.optString(KEY_LAYOUT_DIRECTION) == LAYOUT_DIRECTION_RTL,
                parsingErrorLogger = if (failOnParsingError) {
                    FailingErrorLogger
                } else {
                    ParsingErrorLogger.LOG
                },
            )
        }
    }
}

private object FailingErrorLogger : ParsingErrorLogger {
    override fun logError(e: Exception) {
        throw e
    }
}
