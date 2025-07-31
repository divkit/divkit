// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivAbsoluteEdgeInsets internal constructor(
    @JsonIgnore val bottom: Property<Int>?,
    @JsonIgnore val left: Property<Int>?,
    @JsonIgnore val right: Property<Int>?,
    @JsonIgnore val top: Property<Int>?,
) {

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "bottom" to bottom,
            "left" to left,
            "right" to right,
            "top" to top,
        )
    }
}

fun <T> TemplateContext<T>.divAbsoluteEdgeInsets(): LiteralProperty<DivAbsoluteEdgeInsets> {
    return value(DivAbsoluteEdgeInsets(
        bottom = null,
        left = null,
        right = null,
        top = null,
    ))
}

fun <T> TemplateContext<T>.divAbsoluteEdgeInsets(
    bottom: Property<Int>? = null,
    left: Property<Int>? = null,
    right: Property<Int>? = null,
    top: Property<Int>? = null,
): LiteralProperty<DivAbsoluteEdgeInsets> {
    return value(DivAbsoluteEdgeInsets(
        bottom = bottom,
        left = left,
        right = right,
        top = top,
    ))
}

fun <T> TemplateContext<T>.divAbsoluteEdgeInsets(
    bottom: Int? = null,
    left: Int? = null,
    right: Int? = null,
    top: Int? = null,
): LiteralProperty<DivAbsoluteEdgeInsets> {
    return value(DivAbsoluteEdgeInsets(
        bottom = optionalValue(bottom),
        left = optionalValue(left),
        right = optionalValue(right),
        top = optionalValue(top),
    ))
}

fun CardContext.divAbsoluteEdgeInsets(): DivAbsoluteEdgeInsets {
    return DivAbsoluteEdgeInsets(
        bottom = null,
        left = null,
        right = null,
        top = null,
    )
}

fun CardContext.divAbsoluteEdgeInsets(
    bottom: ValueProperty<Int>? = null,
    left: ValueProperty<Int>? = null,
    right: ValueProperty<Int>? = null,
    top: ValueProperty<Int>? = null,
): DivAbsoluteEdgeInsets {
    return DivAbsoluteEdgeInsets(
        bottom = bottom,
        left = left,
        right = right,
        top = top,
    )
}

fun CardContext.divAbsoluteEdgeInsets(
    bottom: Int? = null,
    left: Int? = null,
    right: Int? = null,
    top: Int? = null,
): DivAbsoluteEdgeInsets {
    return DivAbsoluteEdgeInsets(
        bottom = optionalValue(bottom),
        left = optionalValue(left),
        right = optionalValue(right),
        top = optionalValue(top),
    )
}
