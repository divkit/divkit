// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivLinearGradient internal constructor(
    @JsonIgnore val angle: Property<Int>?,
    @JsonIgnore val colors: Property<List<Color>>?,
) : DivTextGradient, DivBackground {

    @JsonProperty("type") override val type = "gradient"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "angle" to angle,
            "colors" to colors,
        )
    }
}

fun <T> TemplateContext<T>.divLinearGradient(): LiteralProperty<DivLinearGradient> {
    return value(DivLinearGradient(
        angle = null,
        colors = null,
    ))
}

fun <T> TemplateContext<T>.divLinearGradient(
    colors: Property<List<Color>>? = null,
    angle: Property<Int>? = null,
): LiteralProperty<DivLinearGradient> {
    return value(DivLinearGradient(
        angle = angle,
        colors = colors,
    ))
}

fun <T> TemplateContext<T>.divLinearGradient(
    colors: List<Color>? = null,
    angle: Int? = null,
): LiteralProperty<DivLinearGradient> {
    return value(DivLinearGradient(
        angle = optionalValue(angle),
        colors = optionalValue(colors),
    ))
}

fun CardContext.divLinearGradient(
    colors: ValueProperty<List<Color>>,
    angle: ValueProperty<Int>? = null,
): DivLinearGradient {
    return DivLinearGradient(
        angle = angle,
        colors = colors,
    )
}

fun CardContext.divLinearGradient(
    colors: List<Color>,
    angle: Int? = null,
): DivLinearGradient {
    return DivLinearGradient(
        angle = optionalValue(angle),
        colors = value(colors),
    )
}
