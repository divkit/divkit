// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivEdgeInsets internal constructor(
    @JsonIgnore val bottom: Property<Int>?,
    @JsonIgnore val left: Property<Int>?,
    @JsonIgnore val right: Property<Int>?,
    @JsonIgnore val top: Property<Int>?,
    @JsonIgnore val unit: Property<DivSizeUnit>?,
) {

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "bottom" to bottom,
            "left" to left,
            "right" to right,
            "top" to top,
            "unit" to unit,
        )
    }
}

fun <T> TemplateContext<T>.divEdgeInsets(): LiteralProperty<DivEdgeInsets> {
    return value(DivEdgeInsets(
        bottom = null,
        left = null,
        right = null,
        top = null,
        unit = null,
    ))
}

fun <T> TemplateContext<T>.divEdgeInsets(
    bottom: Property<Int>? = null,
    left: Property<Int>? = null,
    right: Property<Int>? = null,
    top: Property<Int>? = null,
    unit: Property<DivSizeUnit>? = null,
): LiteralProperty<DivEdgeInsets> {
    return value(DivEdgeInsets(
        bottom = bottom,
        left = left,
        right = right,
        top = top,
        unit = unit,
    ))
}

fun <T> TemplateContext<T>.divEdgeInsets(
    bottom: Int? = null,
    left: Int? = null,
    right: Int? = null,
    top: Int? = null,
    unit: DivSizeUnit? = null,
): LiteralProperty<DivEdgeInsets> {
    return value(DivEdgeInsets(
        bottom = optionalValue(bottom),
        left = optionalValue(left),
        right = optionalValue(right),
        top = optionalValue(top),
        unit = optionalValue(unit),
    ))
}

fun CardContext.divEdgeInsets(): DivEdgeInsets {
    return DivEdgeInsets(
        bottom = null,
        left = null,
        right = null,
        top = null,
        unit = null,
    )
}

fun CardContext.divEdgeInsets(
    bottom: ValueProperty<Int>? = null,
    left: ValueProperty<Int>? = null,
    right: ValueProperty<Int>? = null,
    top: ValueProperty<Int>? = null,
    unit: ValueProperty<DivSizeUnit>? = null,
): DivEdgeInsets {
    return DivEdgeInsets(
        bottom = bottom,
        left = left,
        right = right,
        top = top,
        unit = unit,
    )
}

fun CardContext.divEdgeInsets(
    bottom: Int? = null,
    left: Int? = null,
    right: Int? = null,
    top: Int? = null,
    unit: DivSizeUnit? = null,
): DivEdgeInsets {
    return DivEdgeInsets(
        bottom = optionalValue(bottom),
        left = optionalValue(left),
        right = optionalValue(right),
        top = optionalValue(top),
        unit = optionalValue(unit),
    )
}
