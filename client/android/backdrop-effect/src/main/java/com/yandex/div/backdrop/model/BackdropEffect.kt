package com.yandex.div.backdrop.model

import com.yandex.div.backdrop.util.optObject
import com.yandex.div.backdrop.util.optionalString
import org.json.JSONException
import org.json.JSONObject

internal class BackdropEffect(
    val backdropId: String? = null,
    val blur: Blur? = null,
    val refraction: Refraction? = null,
    val highlight: Highlight? = null,
    val colorAdjustment: ColorAdjustment? = null,
) {

    companion object {

        @JvmStatic
        @Throws(JSONException::class)
        fun deserialize(json: JSONObject): BackdropEffect {
            return BackdropEffect(
                backdropId = json.optionalString("backdrop_id"),
                blur = json.optObject("blur") { Blur.deserialize(it) },
                refraction = json.optObject("refraction") { Refraction.deserialize(it) },
                highlight = json.optObject("highlight") { Highlight.deserialize(it) },
                colorAdjustment = json.optObject("color_adjustment") { ColorAdjustment.deserialize(it) },
            )
        }
    }
}
