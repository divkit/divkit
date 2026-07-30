package com.yandex.div.backdrop.model

import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import com.yandex.div.backdrop.util.optionalString
import com.yandex.div.evaluable.types.Color
import org.json.JSONException
import org.json.JSONObject

internal sealed interface Highlight {

    @get:FloatRange(from = 0.0) val rimWidth: Double
    @get:FloatRange(from = 0.0, to = 1.0) val alpha: Double

    companion object {

        const val DEFAULT_RIM_WIDTH = 0.5
        const val DEFAULT_ALPHA = 1.0

        @JvmStatic
        @Throws(JSONException::class)
        fun deserialize(json: JSONObject): Highlight {
            return when (val type = json.getString("type")) {
                SpecularHighlight.TYPE -> SpecularHighlight.deserialize(json)
                AmbientHighlight.TYPE -> AmbientHighlight.deserialize(json)
                else -> throw JSONException("Unknown value for key 'type': $type")
            }
        }
    }
}

internal class SpecularHighlight(
    @param:FloatRange(from = 0.0) override val rimWidth: Double = Highlight.DEFAULT_RIM_WIDTH,
    @param:FloatRange(from = 0.0, to = 1.0) override val alpha: Double = Highlight.DEFAULT_ALPHA,
    @param:ColorInt val color: Int = DEFAULT_COLOR,
    val angle: Double = DEFAULT_ANGLE,
    @param:FloatRange(from = 0.0) val falloff: Double = DEFAULT_FALLOFF,
) : Highlight {

    companion object {

        const val TYPE = "specular"

        const val DEFAULT_COLOR = 0x80FFFFFF.toInt()
        const val DEFAULT_ANGLE = 45.0
        const val DEFAULT_FALLOFF = 1.0

        @JvmStatic
        @Throws(JSONException::class)
        fun deserialize(json: JSONObject): SpecularHighlight {
            return SpecularHighlight(
                rimWidth = json.optDouble("rim_width", Highlight.DEFAULT_RIM_WIDTH),
                alpha = json.optDouble("alpha", Highlight.DEFAULT_ALPHA),
                color = json.optionalString("color")?.let { Color.parse(it).value } ?: DEFAULT_COLOR,
                angle = json.optDouble("angle", DEFAULT_ANGLE),
                falloff = json.optDouble("falloff", DEFAULT_FALLOFF),
            )
        }
    }
}

internal class AmbientHighlight(
    @param:FloatRange(from = 0.0) override val rimWidth: Double = Highlight.DEFAULT_RIM_WIDTH,
    @param:FloatRange(from = 0.0, to = 1.0) override val alpha: Double = Highlight.DEFAULT_ALPHA,
    @param:FloatRange(from = 0.0, to = 1.0) val intensity: Double = DEFAULT_INTENSITY,
    val angle: Double = DEFAULT_ANGLE,
) : Highlight {

    companion object {

        const val TYPE = "ambient"

        const val DEFAULT_INTENSITY = 0.4
        const val DEFAULT_ANGLE = 45.0

        @JvmStatic
        @Throws(JSONException::class)
        fun deserialize(json: JSONObject): AmbientHighlight {
            return AmbientHighlight(
                rimWidth = json.optDouble("rim_width", Highlight.DEFAULT_RIM_WIDTH),
                alpha = json.optDouble("alpha", Highlight.DEFAULT_ALPHA),
                intensity = json.optDouble("intensity", DEFAULT_INTENSITY),
                angle = json.optDouble("angle", DEFAULT_ANGLE),
            )
        }
    }
}
