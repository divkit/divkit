package com.yandex.div.backdrop.model

import androidx.annotation.FloatRange
import org.json.JSONException
import org.json.JSONObject

internal class Refraction(
    @param:FloatRange(from = 0.0) val strength: Double = DEFAULT_STRENGTH,
    @param:FloatRange(from = 0.0) val height: Double = DEFAULT_HEIGHT,
    val chromaticAberration: Boolean = DEFAULT_CHROMATIC_ABERRATION,
) {

    companion object {

        const val DEFAULT_STRENGTH = 0.0
        const val DEFAULT_HEIGHT = 0.0
        const val DEFAULT_CHROMATIC_ABERRATION = false

        @JvmStatic
        @Throws(JSONException::class)
        fun deserialize(json: JSONObject): Refraction {
            return Refraction(
                strength = json.optDouble("strength", DEFAULT_STRENGTH),
                height = json.optDouble("height", DEFAULT_HEIGHT),
                chromaticAberration = json.optBoolean("chromatic_aberration", DEFAULT_CHROMATIC_ABERRATION),
            )
        }
    }
}
