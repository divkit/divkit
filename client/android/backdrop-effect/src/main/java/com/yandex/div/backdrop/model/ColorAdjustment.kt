package com.yandex.div.backdrop.model

import androidx.annotation.FloatRange
import org.json.JSONException
import org.json.JSONObject

internal class ColorAdjustment(
    @param:FloatRange(from = 0.0) val brightness: Double = DEFAULT_BRIGHTNESS,
    @param:FloatRange(from = 0.0) val contrast: Double = DEFAULT_CONTRAST,
    @param:FloatRange(from = 0.0) val saturation: Double = DEFAULT_SATURATION,
) {

    companion object {

        const val DEFAULT_BRIGHTNESS = 0.0
        const val DEFAULT_CONTRAST = 1.0
        const val DEFAULT_SATURATION = 1.0

        @JvmStatic
        @Throws(JSONException::class)
        fun deserialize(json: JSONObject): ColorAdjustment {
            return ColorAdjustment(
                brightness = json.optDouble("brightness", DEFAULT_BRIGHTNESS),
                contrast = json.optDouble("contrast", DEFAULT_CONTRAST),
                saturation = json.optDouble("saturation", DEFAULT_SATURATION),
            )
        }
    }
}
