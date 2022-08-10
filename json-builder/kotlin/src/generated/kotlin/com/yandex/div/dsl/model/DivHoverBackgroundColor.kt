// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivHoverBackgroundColor internal constructor(
    @JsonIgnore val color: Property<Color>?,
) : DivHover() {

    @JsonProperty("type") override val type = "background-color"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "color" to color,
        )
    }
}

fun <T> TemplateContext<T>.divHoverBackgroundColor(): LiteralProperty<DivHoverBackgroundColor> {
    return value(DivHoverBackgroundColor(
        color = null,
    ))
}

fun <T> TemplateContext<T>.divHoverBackgroundColor(
    color: Property<Color>? = null,
): LiteralProperty<DivHoverBackgroundColor> {
    return value(DivHoverBackgroundColor(
        color = color,
    ))
}

fun <T> TemplateContext<T>.divHoverBackgroundColor(
    color: Color? = null,
): LiteralProperty<DivHoverBackgroundColor> {
    return value(DivHoverBackgroundColor(
        color = optionalValue(color),
    ))
}

fun CardContext.divHoverBackgroundColor(
    color: ValueProperty<Color>,
): DivHoverBackgroundColor {
    return DivHoverBackgroundColor(
        color = color,
    )
}

fun CardContext.divHoverBackgroundColor(
    color: Color,
): DivHoverBackgroundColor {
    return DivHoverBackgroundColor(
        color = value(color),
    )
}
