package com.yandex.div.backdrop.model

import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import com.yandex.div.backdrop.util.optionalString
import com.yandex.div.evaluable.types.Color
import org.json.JSONException
import org.json.JSONObject

internal sealed interface RimHighlight {

    @get:FloatRange(from = 0.0) val width: Double
    @get:FloatRange(from = 0.0, to = 1.0) val alpha: Double

    companion object {

        const val DEFAULT_WIDTH = 0.5
        const val DEFAULT_ALPHA = 1.0

        @JvmStatic
        @Throws(JSONException::class)
        fun deserialize(json: JSONObject): RimHighlight {
            return when (val type = json.getString("type")) {
                SpecularRimHighlight.TYPE -> SpecularRimHighlight.deserialize(json)
                AmbientRimHighlight.TYPE -> AmbientRimHighlight.deserialize(json)
                else -> throw JSONException("Unknown value for key 'type': $type")
            }
        }
    }
}

internal class SpecularRimHighlight(
    @FloatRange(from = 0.0) override val width: Double = RimHighlight.DEFAULT_WIDTH,
    @FloatRange(from = 0.0, to = 1.0) override val alpha: Double = RimHighlight.DEFAULT_ALPHA,
    @ColorInt val color: Int = DEFAULT_COLOR,
    val angle: Double = DEFAULT_ANGLE,
    @FloatRange(from = 0.0) val falloff: Double = DEFAULT_FALLOFF,
) : RimHighlight {

    companion object {

        const val TYPE = "specular"

        const val DEFAULT_COLOR = 0x80FFFFFF.toInt()
        const val DEFAULT_ANGLE = 45.0
        const val DEFAULT_FALLOFF = 1.0

        @JvmStatic
        @Throws(JSONException::class)
        fun deserialize(json: JSONObject): SpecularRimHighlight {
            return SpecularRimHighlight(
                width = json.optDouble("width", RimHighlight.DEFAULT_WIDTH),
                alpha = json.optDouble("alpha", RimHighlight.DEFAULT_ALPHA),
                color = json.optionalString("color")?.let { Color.parse(it).value } ?: DEFAULT_COLOR,
                angle = json.optDouble("angle", DEFAULT_ANGLE),
                falloff = json.optDouble("falloff", DEFAULT_FALLOFF),
            )
        }
    }
}

internal class AmbientRimHighlight(
    @FloatRange(from = 0.0) override val width: Double = RimHighlight.DEFAULT_WIDTH,
    @FloatRange(from = 0.0, to = 1.0) override val alpha: Double = RimHighlight.DEFAULT_ALPHA,
    @FloatRange(from = 0.0, to = 1.0) val intensity: Double = DEFAULT_INTENSITY,
    val angle: Double = DEFAULT_ANGLE,
) : RimHighlight {

    companion object {

        const val TYPE = "ambient"

        const val DEFAULT_INTENSITY = 0.4
        const val DEFAULT_ANGLE = 45.0

        @JvmStatic
        @Throws(JSONException::class)
        fun deserialize(json: JSONObject): AmbientRimHighlight {
            return AmbientRimHighlight(
                width = json.optDouble("width", RimHighlight.DEFAULT_WIDTH),
                alpha = json.optDouble("alpha", RimHighlight.DEFAULT_ALPHA),
                intensity = json.optDouble("intensity", DEFAULT_INTENSITY),
                angle = json.optDouble("angle", DEFAULT_ANGLE),
            )
        }
    }
}
