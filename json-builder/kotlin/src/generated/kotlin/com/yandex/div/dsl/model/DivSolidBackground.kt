// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivSolidBackground internal constructor(
    @JsonIgnore val color: Property<Color>?,
) : DivBackground() {

    @JsonProperty("type") override val type = "solid"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "color" to color,
        )
    }
}

fun <T> TemplateContext<T>.divSolidBackground(): LiteralProperty<DivSolidBackground> {
    return value(DivSolidBackground(
        color = null,
    ))
}

fun <T> TemplateContext<T>.divSolidBackground(
    color: Property<Color>? = null,
): LiteralProperty<DivSolidBackground> {
    return value(DivSolidBackground(
        color = color,
    ))
}

fun <T> TemplateContext<T>.divSolidBackground(
    color: Color? = null,
): LiteralProperty<DivSolidBackground> {
    return value(DivSolidBackground(
        color = optionalValue(color),
    ))
}

fun CardContext.divSolidBackground(
    color: ValueProperty<Color>,
): DivSolidBackground {
    return DivSolidBackground(
        color = color,
    )
}

fun CardContext.divSolidBackground(
    color: Color,
): DivSolidBackground {
    return DivSolidBackground(
        color = value(color),
    )
}
