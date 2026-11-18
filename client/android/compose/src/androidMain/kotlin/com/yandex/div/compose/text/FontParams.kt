package com.yandex.div.compose.text

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.font.FontWeight
import com.yandex.div2.DivFontWeight
import org.json.JSONObject

@ConsistentCopyVisibility
@Immutable
internal data class FontParams private constructor(
    val family: String?,
    val weight: FontWeight?,
    val variationSettings: JSONObject?,
) {
    constructor(
        family: String?,
        weight: DivFontWeight?,
        weightValue: Int?,
        variationSettings: JSONObject?,
    ) : this(
        family = family,
        weight = weight.toFontWeight(weightValue),
        variationSettings = variationSettings,
    )
}
