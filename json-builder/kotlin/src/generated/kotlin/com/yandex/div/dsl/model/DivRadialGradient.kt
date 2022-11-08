// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivRadialGradient internal constructor(
    @JsonIgnore val centerX: Property<DivRadialGradientCenter>?,
    @JsonIgnore val centerY: Property<DivRadialGradientCenter>?,
    @JsonIgnore val colors: Property<List<Color>>?,
    @JsonIgnore val radius: Property<DivRadialGradientRadius>?,
) : DivTextGradient, DivBackground {

    @JsonProperty("type") override val type = "radial_gradient"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "center_x" to centerX,
            "center_y" to centerY,
            "colors" to colors,
            "radius" to radius,
        )
    }
}

fun <T> TemplateContext<T>.divRadialGradient(): LiteralProperty<DivRadialGradient> {
    return value(DivRadialGradient(
        centerX = null,
        centerY = null,
        colors = null,
        radius = null,
    ))
}

fun <T> TemplateContext<T>.divRadialGradient(
    colors: Property<List<Color>>? = null,
    centerX: Property<DivRadialGradientCenter>? = null,
    centerY: Property<DivRadialGradientCenter>? = null,
    radius: Property<DivRadialGradientRadius>? = null,
): LiteralProperty<DivRadialGradient> {
    return value(DivRadialGradient(
        centerX = centerX,
        centerY = centerY,
        colors = colors,
        radius = radius,
    ))
}

fun <T> TemplateContext<T>.divRadialGradient(
    colors: List<Color>? = null,
    centerX: DivRadialGradientCenter? = null,
    centerY: DivRadialGradientCenter? = null,
    radius: DivRadialGradientRadius? = null,
): LiteralProperty<DivRadialGradient> {
    return value(DivRadialGradient(
        centerX = optionalValue(centerX),
        centerY = optionalValue(centerY),
        colors = optionalValue(colors),
        radius = optionalValue(radius),
    ))
}

fun CardContext.divRadialGradient(
    colors: ValueProperty<List<Color>>,
    centerX: ValueProperty<DivRadialGradientCenter>? = null,
    centerY: ValueProperty<DivRadialGradientCenter>? = null,
    radius: ValueProperty<DivRadialGradientRadius>? = null,
): DivRadialGradient {
    return DivRadialGradient(
        centerX = centerX,
        centerY = centerY,
        colors = colors,
        radius = radius,
    )
}

fun CardContext.divRadialGradient(
    colors: List<Color>,
    centerX: DivRadialGradientCenter? = null,
    centerY: DivRadialGradientCenter? = null,
    radius: DivRadialGradientRadius? = null,
): DivRadialGradient {
    return DivRadialGradient(
        centerX = optionalValue(centerX),
        centerY = optionalValue(centerY),
        colors = value(colors),
        radius = optionalValue(radius),
    )
}
