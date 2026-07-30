package com.yandex.div.backdrop.model

import androidx.annotation.FloatRange
import org.json.JSONException
import org.json.JSONObject

internal class Blur(
    @param:FloatRange(from = 0.0) val radius: Double = DEFAULT_RADIUS,
) {

    companion object {

        const val DEFAULT_RADIUS = 0.0

        @JvmStatic
        @Throws(JSONException::class)
        fun deserialize(json: JSONObject): Blur {
            return Blur(
                radius = json.optDouble("radius", DEFAULT_RADIUS),
            )
        }
    }
}
