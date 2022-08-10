// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivStroke internal constructor(
    @JsonIgnore val color: Property<Color>?,
    @JsonIgnore val unit: Property<DivSizeUnit>?,
    @JsonIgnore val width: Property<Int>?,
) {

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "color" to color,
            "unit" to unit,
            "width" to width,
        )
    }
}

fun <T> TemplateContext<T>.divStroke(): LiteralProperty<DivStroke> {
    return value(DivStroke(
        color = null,
        unit = null,
        width = null,
    ))
}

fun <T> TemplateContext<T>.divStroke(
    color: Property<Color>? = null,
    unit: Property<DivSizeUnit>? = null,
    width: Property<Int>? = null,
): LiteralProperty<DivStroke> {
    return value(DivStroke(
        color = color,
        unit = unit,
        width = width,
    ))
}

fun <T> TemplateContext<T>.divStroke(
    color: Color? = null,
    unit: DivSizeUnit? = null,
    width: Int? = null,
): LiteralProperty<DivStroke> {
    return value(DivStroke(
        color = optionalValue(color),
        unit = optionalValue(unit),
        width = optionalValue(width),
    ))
}

fun CardContext.divStroke(
    color: ValueProperty<Color>,
    unit: ValueProperty<DivSizeUnit>? = null,
    width: ValueProperty<Int>? = null,
): DivStroke {
    return DivStroke(
        color = color,
        unit = unit,
        width = width,
    )
}

fun CardContext.divStroke(
    color: Color,
    unit: DivSizeUnit? = null,
    width: Int? = null,
): DivStroke {
    return DivStroke(
        color = value(color),
        unit = optionalValue(unit),
        width = optionalValue(width),
    )
}
