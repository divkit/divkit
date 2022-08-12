// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivGradientBackground internal constructor(
    @JsonIgnore val angle: Property<Int>?,
    @JsonIgnore val colors: Property<List<Color>>?,
) : DivBackground() {

    @JsonProperty("type") override val type = "gradient"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "angle" to angle,
            "colors" to colors,
        )
    }
}

fun <T> TemplateContext<T>.divGradientBackground(): LiteralProperty<DivGradientBackground> {
    return value(DivGradientBackground(
        angle = null,
        colors = null,
    ))
}

fun <T> TemplateContext<T>.divGradientBackground(
    colors: Property<List<Color>>? = null,
    angle: Property<Int>? = null,
): LiteralProperty<DivGradientBackground> {
    return value(DivGradientBackground(
        angle = angle,
        colors = colors,
    ))
}

fun <T> TemplateContext<T>.divGradientBackground(
    colors: List<Color>? = null,
    angle: Int? = null,
): LiteralProperty<DivGradientBackground> {
    return value(DivGradientBackground(
        angle = optionalValue(angle),
        colors = optionalValue(colors),
    ))
}

fun CardContext.divGradientBackground(
    colors: ValueProperty<List<Color>>,
    angle: ValueProperty<Int>? = null,
): DivGradientBackground {
    return DivGradientBackground(
        angle = angle,
        colors = colors,
    )
}

fun CardContext.divGradientBackground(
    colors: List<Color>,
    angle: Int? = null,
): DivGradientBackground {
    return DivGradientBackground(
        angle = optionalValue(angle),
        colors = value(colors),
    )
}
